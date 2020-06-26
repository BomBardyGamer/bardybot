package dev.bombardy.bardybot.spring

import dev.bombardy.bardybot.commands.PlayCommand
import me.mattstudios.mfjda.base.CommandManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommandService @Autowired constructor(commandManager: CommandManager,
                                            config: DiscordConfig,
                                            trackService: TrackService
) {

    init {
        commandManager.register(PlayCommand(trackService, config.prefix))
    }
}