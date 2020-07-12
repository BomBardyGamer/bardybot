package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.getLogger
import dev.bombardy.bardybot.services.TrackService
import dev.bombardy.octo.command.Command
import net.dv8tion.jda.api.entities.Message

class PauseCommand(private val trackService: TrackService) : Command(listOf("pause", "stop", "shutup", "stfu"), true) {

    override suspend fun execute(message: Message, arguments: List<String>) {
        val audioPlayer = trackService.getMusicManager(message.textChannel.guild.id).player

        if (!audioPlayer.isPaused) {
            LOGGER.debug("Attempting to pause currently playing track ${audioPlayer.playingTrack}")
            audioPlayer.isPaused = true
            message.channel.sendMessage("**Ah come on, I was listening to that!**").queue()
        }
    }

    companion object {
        private val LOGGER = getLogger<PauseCommand>()
    }
}