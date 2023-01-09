package me.bardy.bot.commands.music

import me.bardy.bot.command.BasicCommand
import java.time.Duration
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.util.BardyBotColors
import me.bardy.bot.util.author
import me.bardy.bot.util.color
import me.bardy.bot.util.Durations
import me.bardy.bot.util.description
import me.bardy.bot.util.embed
import me.bardy.bot.audio.GuildMusicManagers
import me.bardy.bot.util.thumbnail
import net.dv8tion.jda.api.entities.Member
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component

@Component
class NowPlayingCommand(private val musicManagers: GuildMusicManagers) : BasicCommand("nowplaying", setOf("np")) {

    override fun execute(context: BotCommandContext) {
        val manager = musicManagers.getByGuild(context.guild)

        val nowPlaying = manager.playingTrack()
        if (nowPlaying == null) {
            context.reply("The party hasn't started yet, there's a play command you can use to kick it off!")
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
            author("What I've got on", "https://bot.bardy.me", "https://cdn.prevarinite.com/images/bbg.jpg")
            thumbnail("https://img.youtube.com/vi/${nowPlaying.identifier}/maxresdefault.jpg")
            description("""
                Got [${nowPlaying.info.title}](${nowPlaying.info.uri}) playing now!

                We're this far in:
                `${calculateBar(positionMillis.toDouble() / nowPlaying.duration)}`

                `${Durations.formatHumanReadable(position)} / ${Durations.formatHumanReadable(duration)}`

                *Who put it on? ${formatMemberName(requester)} did!*
            """.trimIndent())
            color(BardyBotColors.BARDY_ORANGE)
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

        private val LOGGER = LogManager.getLogger()

        @JvmStatic
        private fun formatMemberName(member: Member): String {
            var result = member.user.asTag
            if (member.nickname != null) result += " (also known as ${member.nickname})"
            return result
        }
    }
}
