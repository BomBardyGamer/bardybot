package me.bardy.bot.services

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.tree.LiteralCommandNode
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import org.springframework.cache.Cache
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct

/**
 * Handles command registration. Will do more in the future.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Service
class CommandService(
    private val dispatcher: CommandDispatcher<CommandContext>,
    private val commands: Set<Command>
) {

    @PostConstruct
    fun registerCommands() = commands.forEach { command ->
        val node = command.register()
        dispatcher.root.addChild(node)

        command.aliases.forEach { node.buildRedirect(it) }
    }

    private fun LiteralCommandNode<CommandContext>.buildRedirect(alias: String) =
        literal<CommandContext>(alias.toLowerCase(Locale.ENGLISH))
            .requires(requirement)
            .forward(redirect, redirectModifier, isFork)
            .executes(command)
            .apply { children.forEach { then(it) } }
            .build()
}