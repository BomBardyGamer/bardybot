package me.bardy.bot.commands.music

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.time.Duration
import kotlin.random.Random
import me.bardy.bot.command.Command
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.command.argument
import me.bardy.bot.command.getArgument
import me.bardy.bot.command.literal
import me.bardy.bot.command.runs
import me.bardy.bot.util.Durations
import me.bardy.bot.util.buildEmbed
import me.bardy.bot.util.color
import me.bardy.bot.util.description
import me.bardy.bot.audio.GuildMusicManagers
import me.bardy.bot.util.title
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component

@Component
class QueueCommand(private val musicManagers: GuildMusicManagers) : Command(setOf("q")) {

    override fun create(): LiteralArgumentBuilder<BotCommandContext> = literal("queue") {
        argument("page", IntegerArgumentType.integer()) {
            runs { paginateQueue(it.source, it.getArgument("page")) }
        }
        runs { paginateQueue(it.source, 1) }
    }

    private fun paginateQueue(context: BotCommandContext, pageNumber: Int) {
        val member = context.member ?: return
        val musicManager = musicManagers.getByGuild(context.guild)

        val paginatedQueue = musicManager.paginateQueue(10)
        val embed = buildEmbed {
            title("Here's what I've got lined up for you in ${context.guild.name}", "https://bot.bardy.me")
            color(Random.nextInt(RGB_MAX_VALUE))
        }

        if (pageNumber == 1) {
            embed.description("\n\n__What's on now:__\n")

            val nowPlaying = musicManager.playingTrack()
            if (nowPlaying == null) {
                embed.appendDescription("You still haven't queued anything! Come on, let's get this party started! \uD83C\uDF89")
            } else {
                val requester = nowPlaying.userData as? Member
                if (requester == null) {
                    LOGGER.error("User data for requested track $nowPlaying was not of type Member.")
                    return
                }

                appendTrack(embed, nowPlaying, requester, -1)
            }

            if (paginatedQueue.isEmpty()) {
                context.reply(embed.setFooter("Page 1/1", member.user.avatarUrl).build())
                return
            }
        }

        embed.appendDescription("\n__What I've got lined up:__\n")

        paginatedQueue.forEach { page ->
            page.forEachIndexed { i, track ->
                val requester = track.getUserData(Member::class.java)
                if (requester == null) {
                    LOGGER.error("User data for requested track $track was not of type Member.")
                    return
                }

                appendTrack(embed, track, requester, i + 1)
            }
        }

        if (musicManager.hasQueuedTracks()) {
            val plural = if (musicManager.queuedTrackCount() == 1) "tune" else "tunes"
            val length = Durations.formatHumanReadable(Duration.ofMillis(musicManager.getTotalQueuedTime()))
            embed.appendDescription("\n**${musicManager.queuedTrackCount()} banging $plural ready to be played, $length long**")
        }

        context.reply(embed.setFooter("Page $pageNumber / ${paginatedQueue.size}", member.user.avatarUrl).build())
    }

    private fun appendTrack(embed: EmbedBuilder, track: AudioTrack, requester: Member, number: Int) {
        if (number != -1) {
            embed.appendDescription("\n`$number`. ")
        } else {
            embed.appendDescription("\n")
        }

        val duration = Durations.formatHumanReadable(track.duration)
        embed.appendDescription("[${track.info.title}](${track.info.uri}) | `$duration`\n")
        embed.appendDescription("*Who put it on? ${formatMemberName(requester)} did!*\n\n")
    }

    companion object {

        private const val RGB_MAX_VALUE = 16777216
        private val LOGGER = LogManager.getLogger()

        @JvmStatic
        private fun formatMemberName(member: Member): String {
            var result = member.user.asTag
            if (member.nickname != null) result += " (also known as ${member.nickname})"
            return result
        }
    }
}
