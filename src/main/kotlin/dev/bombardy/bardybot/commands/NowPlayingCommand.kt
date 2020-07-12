package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.format
import dev.bombardy.bardybot.formatName
import dev.bombardy.bardybot.getLogger
import dev.bombardy.bardybot.services.TrackService
import dev.bombardy.octo.command.Command
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import java.awt.Color
import kotlin.time.milliseconds

class NowPlayingCommand(private val trackService: TrackService) : Command(listOf("nowplaying", "np"), true) {

    override suspend fun execute(message: Message, arguments: List<String>) {
        val channel = message.textChannel
        val audioPlayer = trackService.getMusicManager(message.guild.id).player

        val nowPlaying = audioPlayer.playingTrack
                ?: return channel.sendMessage("**The party hasn't started yet, there's a play command you can use to kick it off!**").queue()
        val requester = nowPlaying.userData as? Member
                ?: return LOGGER.error("User data for requested track $nowPlaying should have been of type Member and wasn't, please report to creator.")
        val position = audioPlayer.trackPosition

        val formattedPosition = position.milliseconds.format()
        val duration = nowPlaying.duration.milliseconds.format()

        val percentage = (position.toDouble() / nowPlaying.duration)

        val embed = EmbedBuilder()
                .setAuthor("What banging tune I've got on", "https://bot.bardy.me", "https://cdn.prevarinite.com/images/bbg.jpg")
                .setThumbnail("https://img.youtube.com/vi/${nowPlaying.identifier}/maxresdefault.jpg")
                .setDescription("""
                    Got [${nowPlaying.info.title}](${nowPlaying.info.uri}) playing now!

                    We're this far through:
                    `${calculateBar(percentage)}`
                    
                    `$formattedPosition / $duration`
                    
                    *Who put it on? ${requester.formatName()} did!*
                """.trimIndent())
                .setColor(16737792)
                .build()

        channel.sendMessage(embed).queue()
    }

    private fun calculateBar(percentage: Double): String {
        val position = (percentage * BAR_LENGTH).toInt()
        val output = StringBuilder()

        repeat(BAR_LENGTH) {
            when (it) {
                position -> output.append(DOT)
                else -> output.append(DASH)
            }
        }

        return output.toString()
    }

    companion object {
        const val DOT = "\uD83D\uDD18"
        const val DASH = "\u25AC"
        const val BAR_LENGTH = 31

        private val LOGGER = getLogger<NowPlayingCommand>()
    }
}