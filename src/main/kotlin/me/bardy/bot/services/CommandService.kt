package me.bardy.bot.services

import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandManager
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

/**
 * Handles command registration. Will do more in the future.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Service
class CommandService(
    private val commandManager: CommandManager,
    private val commands: Set<Command>
) {

    @PostConstruct
    fun registerCommands() = commandManager.registerAll(commands)
}