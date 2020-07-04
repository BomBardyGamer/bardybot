package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.services.TrackService
import dev.bombardy.octo.command.Command
import net.dv8tion.jda.api.entities.Message

class PauseCommand(private val trackService: TrackService) : Command(listOf("pause"), true) {

    override fun execute(message: Message, arguments: List<String>) {
        if (!trackService.isPaused) {
            trackService.isPaused = true
            message.channel.sendMessage("**I've paused the music,** *for now...*").queue()
        }
    }
}