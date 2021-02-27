package me.bardy.bot.commands

import me.bardy.bot.logger
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandOptions
import me.bardy.bot.services.TrackService
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component

@Component
class PauseCommand(private val trackService: TrackService) : Command("pause") {

    override val options = CommandOptions(listOf("stop", "shutup", "stfu"), true)

    override suspend fun execute(message: Message, channel: TextChannel, arguments: List<String>) {
        val audioPlayer = trackService.getMusicManager(message.textChannel.guild.id).player

        if (!audioPlayer.isPaused) {
            LOGGER.debug("Attempting to pause currently playing track ${audioPlayer.playingTrack}")
            audioPlayer.isPaused = true
            message.channel.sendMessage("**Ah come on, I was listening to that!**").queue()
        }
    }

    companion object {

        private val LOGGER = logger<PauseCommand>()
    }
}