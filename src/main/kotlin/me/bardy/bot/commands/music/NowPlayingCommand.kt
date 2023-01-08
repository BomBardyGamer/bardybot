package me.bardy.bot.commands.music

import me.bardy.bot.command.BasicCommand
import java.time.Duration
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.util.author
import me.bardy.bot.util.color
import me.bardy.bot.util.Colors
import me.bardy.bot.util.description
import me.bardy.bot.util.embed
import me.bardy.bot.util.format
import me.bardy.bot.util.formatName
import me.bardy.bot.util.logger
import me.bardy.bot.util.GuildMusicManagers
import me.bardy.bot.util.thumbnail
import net.dv8tion.jda.api.entities.Member
import org.springframework.stereotype.Component

@Component
class NowPlayingCommand(private val musicManagers: GuildMusicManagers) : BasicCommand("nowplaying", setOf("np")) {

    override fun execute(context: BotCommandContext) {
        val manager = musicManagers.getByGuild(context.guild)

        val nowPlaying = manager.playingTrack()
        if (nowPlaying == null) {
            context.reply("**The party hasn't started yet, there's a play command you can use to kick it off!**")
            return
        }

        val requester = nowPlaying.getUserData(Member::class.java)
        if (requester == null) {
            LOGGER.error("User data for requested track $nowPlaying should have been of type Member and wasn't")
            return
        }

        val positionMillis = manager.trackPosition()
        val position = Duration.ofMillis(positionMillis)
        val duration = Duration.ofMillis(nowPlaying.duration)
        context.reply(embed {
            author("What banging tune I've got on", "https://bot.bardy.me", "https://cdn.prevarinite.com/images/bbg.jpg")
            thumbnail("https://img.youtube.com/vi/${nowPlaying.identifier}/maxresdefault.jpg")
            description("""
                Got [${nowPlaying.info.title}](${nowPlaying.info.uri}) playing now!

                We're this far through:
                `${calculateBar(positionMillis.toDouble() / nowPlaying.duration)}`

                `${position.format()} / ${duration.format()}`

                *Who put it on? ${requester.formatName()} did!*
            """.trimIndent())
            color(Colors.BARDY_ORANGE)
        })
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

        private const val DOT = "\uD83D\uDD18"
        private const val DASH = "\u25AC"
        private const val BAR_LENGTH = 31

        private val LOGGER = logger<NowPlayingCommand>()
    }
}
