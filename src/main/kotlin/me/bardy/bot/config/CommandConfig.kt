package me.bardy.bot.config

import com.mojang.brigadier.CommandDispatcher
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommandConfig(private val commands: Set<Command>) {

    @Bean
    fun commandDispatcher(): CommandDispatcher<CommandContext> {
        val dispatcher = CommandDispatcher<CommandContext>()
        commands.forEach { dispatcher.root.addChild(it.register()) }
        return dispatcher
    }
}
