package me.bardy.bot.services

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import javax.annotation.PostConstruct
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import org.springframework.stereotype.Service

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
    fun registerCommands() {
        commands.forEach { command ->
            val node = command.register()
            dispatcher.root.addChild(node)
            command.aliases.forEach { dispatcher.root.addChild(LiteralArgumentBuilder.literal<CommandContext>(it).redirect(node).build()) }
        }
    }
}
