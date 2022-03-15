package me.bardy.bot.commands.music

import com.mojang.brigadier.tree.LiteralCommandNode
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import me.bardy.bot.components.ManagerMap
import me.bardy.bot.util.Colors
import me.bardy.bot.util.author
import me.bardy.bot.util.color
import me.bardy.bot.util.description
import me.bardy.bot.util.embed
import me.bardy.bot.util.format
import me.bardy.bot.util.formatName
import me.bardy.bot.util.logger
import me.bardy.bot.util.thumbnail
import net.dv8tion.jda.api.entities.Member
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class NowPlayingCommand(private val musicManagers: ManagerMap) : Command("np") {

    override fun register(): LiteralCommandNode<CommandContext> = default("nowplaying") {
        val audioPlayer = musicManagers.get(it.guild.id).player

        val nowPlaying = audioPlayer.playingTrack
        if (nowPlaying == null) {
            it.reply("**The party hasn't started yet, there's a play command you can use to kick it off!**")
            return@default
        }

        val requester = nowPlaying.userData as? Member
        if (requester == null) {
            LOGGER.error("User data for requested track $nowPlaying should have been of type Member and wasn't")
            return@default
        }

        val positionMillis = audioPlayer.trackPosition
        val position = Duration.ofMillis(positionMillis)
        val duration = Duration.ofMillis(nowPlaying.duration)
        it.reply(embed {
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
