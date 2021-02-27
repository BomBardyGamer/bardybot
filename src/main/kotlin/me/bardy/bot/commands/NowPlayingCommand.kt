package me.bardy.bot.commands

import me.bardy.bot.BARDY_ORANGE
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandOptions
import me.bardy.bot.dsl.*
import me.bardy.bot.format
import me.bardy.bot.formatted
import me.bardy.bot.logger
import me.bardy.bot.services.TrackService
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component
import kotlin.time.milliseconds

@Component
class NowPlayingCommand(private val trackService: TrackService) : Command("nowplaying") {

    override val options = CommandOptions(listOf("np"), true)

    override suspend fun execute(message: Message, channel: TextChannel, arguments: List<String>) {
        val audioPlayer = trackService.getMusicManager(message.guild.id).player

        val nowPlaying = audioPlayer.playingTrack
        if (nowPlaying == null) {
            channel.sendMessage("**The party hasn't started yet, there's a play command you can use to kick it off!**").queue()
            return
        }

        val requester = nowPlaying.userData as? Member
        if (requester == null) {
            LOGGER.error("User data for requested track $nowPlaying should have been of type Member and wasn't, please report to creator.")
            return
        }

        val position = audioPlayer.trackPosition
        channel.sendMessage(embed {
            author("What banging tune I've got on", "https://bot.bardy.me", "https://cdn.prevarinite.com/images/bbg.jpg")
            thumbnail = "https://img.youtube.com/vi/${nowPlaying.identifier}/maxresdefault.jpg"
            description = """
                Got [${nowPlaying.info.title}](${nowPlaying.info.uri}) playing now!

                We're this far through:
                `${calculateBar(position.toDouble() / nowPlaying.duration)}`

                `${position.milliseconds.format()} / ${nowPlaying.duration.milliseconds.format()}`

                *Who put it on? ${requester.formatted} did!*
            """.trimIndent()
            color = BARDY_ORANGE
        }).queue()
    }

    private fun calculateBar(percentage: Double): String {
        val position = (percentage * BAR_LENGTH).toInt()
        val output = StringBuilder()

        repeat(BAR_LENGTH) {
            if (it == position) output.append(DOT) else output.append(DASH)
        }

        return output.toString()
    }

    companion object {

        const val DOT = "\uD83D\uDD18"
        const val DASH = "\u25AC"
        const val BAR_LENGTH = 31

        private val LOGGER = logger<NowPlayingCommand>()
    }
}