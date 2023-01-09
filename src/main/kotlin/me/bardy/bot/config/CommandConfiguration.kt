package me.bardy.bot.config

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import me.bardy.bot.command.Command
import me.bardy.bot.command.BotCommandContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommandConfiguration {

    @Bean
    fun commandDispatcher(commands: Set<Command>): CommandDispatcher<BotCommandContext> {
        val dispatcher = CommandDispatcher<BotCommandContext>()
        commands.forEach { command ->
            val node = command.create().build()
            dispatcher.root.addChild(node)
            command.aliases.forEach { dispatcher.register(createAliasNode(it, node)) }
        }
        return dispatcher
    }

    private fun <S> createAliasNode(alias: String, targetNode: CommandNode<S>): LiteralArgumentBuilder<S> =
        LiteralArgumentBuilder.literal<S>(alias).redirect(targetNode)
}
