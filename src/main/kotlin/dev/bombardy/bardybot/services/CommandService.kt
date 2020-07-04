package dev.bombardy.bardybot.services

import dev.bombardy.bardybot.commands.PauseCommand
import dev.bombardy.bardybot.commands.PlayCommand
import dev.bombardy.octo.command.CommandManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Handles command registration. Will do more in the future.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Service
class CommandService @Autowired constructor(
        commandManager: CommandManager,
        trackService: TrackService
) {

    init {
        commandManager.register(PlayCommand(trackService))
        commandManager.register(PauseCommand(trackService))
    }
}