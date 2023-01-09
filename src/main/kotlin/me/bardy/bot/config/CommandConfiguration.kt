package me.bardy.bot.config

import com.mojang.brigadier.CommandDispatcher
import me.bardy.bot.command.Command
import me.bardy.bot.command.BotCommandContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommandConfiguration {

    @Bean
    fun commandDispatcher(commands: Set<Command>): CommandDispatcher<BotCommandContext> {
        val dispatcher = CommandDispatcher<BotCommandContext>()
        commands.forEach { dispatcher.register(it.create()) }
        return dispatcher
    }
}
