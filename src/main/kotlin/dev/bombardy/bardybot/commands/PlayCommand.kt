package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.audio.handle
import dev.bombardy.bardybot.getLogger
import dev.bombardy.bardybot.services.TrackService
import dev.bombardy.octo.command.Command
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import java.awt.Color

/**
 * Handles command execution for the play command, which plays music through the bot.
 *
 * @author BomBardyGamer
 * @since 1.0
 */
class PlayCommand(
        private val trackService: TrackService,
        prefix: String
) : Command(listOf("play", "p"), true) {

    override val helpMessage = MessageBuilder().setEmbed(EmbedBuilder()
            .setDescription("""
                **You got it wrong, here's how you use it:**

                ${prefix}play [Link or query]
            """.trimIndent())
            .setColor(Color.RED)
            .build()
    ).build()

    override suspend fun execute(message: Message, arguments: List<String>) {
        val member = message.member ?: return
        val channel = message.textChannel
        val audioPlayer = trackService.getMusicManager(channel.guild.id).player

        if (arguments.isEmpty()) {
            if (audioPlayer.isPaused) {
                LOGGER.debug("Attempting to resume paused track ${audioPlayer.playingTrack}")
                audioPlayer.isPaused = false

                channel.sendMessage("**About time you played that again, I was starting to get bored!**").queue()
                return
            }
            channel.sendMessage(helpMessage).queue()
            return
        }

        if (audioPlayer.isPaused) {
            channel.sendMessage("**Just to let you know, I'm still on pause from earlier!**").queue()
        }

        LOGGER.debug("Delegating track load to TrackService. Provided arguments: channel - $channel, arguments - $arguments, requester - $member")
        trackService.loadTrack(channel, arguments.joinToString(" "), member).handle(channel)
    }

    companion object {
        private val LOGGER = getLogger<PlayCommand>()
    }
}