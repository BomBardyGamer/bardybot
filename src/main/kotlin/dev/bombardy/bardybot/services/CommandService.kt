package dev.bombardy.bardybot.services

import dev.bombardy.bardybot.commands.*
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
        commandManager.register(PlayCommand(trackService, commandManager.prefix))
        commandManager.register(PauseCommand(trackService))
        commandManager.register(SkipCommand(trackService))
        commandManager.register(QueueCommand(trackService))
        commandManager.register(LoopCommand(trackService))
        commandManager.register(NowPlayingCommand(trackService))
        commandManager.register(VolumeCommand(trackService))
    }
}