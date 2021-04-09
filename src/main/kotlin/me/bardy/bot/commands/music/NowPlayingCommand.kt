package me.bardy.bot.commands.music

import com.mojang.brigadier.tree.LiteralCommandNode
import me.bardy.bot.BARDY_ORANGE
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import me.bardy.bot.components.ManagerMap
import me.bardy.bot.dsl.*
import me.bardy.bot.format
import me.bardy.bot.formatted
import me.bardy.bot.logger
import net.dv8tion.jda.api.entities.Member
import org.springframework.stereotype.Component
import kotlin.time.milliseconds

@Component
class NowPlayingCommand(private val musicManagers: ManagerMap) : Command("np") {

    override fun register(): LiteralCommandNode<CommandContext> = default("nowplaying") {
        val audioPlayer = musicManagers[it.guild.id].player

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

        val position = audioPlayer.trackPosition
        it.reply(embed {
            author("What banging tune I've got on", "https://bot.bardy.me", "https://cdn.prevarinite.com/images/bbg.jpg")
            thumbnail("https://img.youtube.com/vi/${nowPlaying.identifier}/maxresdefault.jpg")
            description("""
                Got [${nowPlaying.info.title}](${nowPlaying.info.uri}) playing now!

                We're this far through:
                `${calculateBar(position.toDouble() / nowPlaying.duration)}`

                `${position.milliseconds.format()} / ${nowPlaying.duration.milliseconds.format()}`

                *Who put it on? ${requester.formatted} did!*
            """.trimIndent())
            color(BARDY_ORANGE)
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

        const val DOT = "\uD83D\uDD18"
        const val DASH = "\u25AC"
        const val BAR_LENGTH = 31

        private val LOGGER = logger<NowPlayingCommand>()
    }
}