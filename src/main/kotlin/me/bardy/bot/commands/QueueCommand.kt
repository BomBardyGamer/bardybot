package me.bardy.bot.commands

import io.sentry.Sentry
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandOptions
import me.bardy.bot.format
import me.bardy.bot.formatted
import me.bardy.bot.logger
import me.bardy.bot.services.TrackService
import me.bardy.bot.sumBy
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component
import java.util.concurrent.ThreadLocalRandom
import kotlin.time.milliseconds

@Component
class QueueCommand(private val trackService: TrackService) : Command("queue") {

    override val options = CommandOptions(listOf("q"), true)

    override suspend fun execute(message: Message, channel: TextChannel, arguments: List<String>) {
        val musicManager = trackService.getMusicManager(channel.guild.id)

        val member = message.member ?: return
        val queue = musicManager.scheduler.queue

        val queueAsPages = queue.chunked(10)
        val embed = EmbedBuilder()
                .setTitle("Here's what I've got lined up for you in ${message.guild.name}", "https://bot.bardy.me/")
                .setColor(ThreadLocalRandom.current().nextInt(RGB_MAX_VALUE))

        val pageNumber = runCatching {
            arguments.getOrElse(0) { "1" }.toInt()
        }.getOrElse {
            channel.sendMessage("**I can't lookup queue results by page number if you don't give me a number!**").queue()
            return
        }

        if (pageNumber == 1) {
            embed.setDescription("""

            __What I'm playing now:__

            """.trimIndent())

            val nowPlaying = musicManager.player.playingTrack

            if (nowPlaying == null) {
                embed.appendDescription("You still haven't queued anything! Come on, let's get this party started! \uD83C\uDF89")
                embed.setFooter("Page 1/1", member.user.avatarUrl)

                channel.sendMessage(embed.build()).queue()
                return
            } else {
                val requester = nowPlaying.userData as? Member

                if (requester == null) {
                    Sentry.capture("User data for requested track $nowPlaying should have been of type Member and wasn't.")
                    LOGGER.error("User data for requested track $nowPlaying should have been of type Member and wasn't, please report to creator.")
                    return
                }

                embed.appendDescription("""
                    [${nowPlaying.info.title}](${nowPlaying.info.uri}) | `${nowPlaying.duration.milliseconds.format()}`
                    *Who put it on? ${requester.formatted} did!*

                """.trimIndent())
            }

            if (queueAsPages.isEmpty()) {
                embed.setFooter("Page 1/1", member.user.avatarUrl)

                channel.sendMessage(embed.build()).queue()
                return
            }
        }

        embed.appendDescription("""
            
            __What I've got for you next:__
        """.trimIndent())

        var itemNumber = 1
        queueAsPages[pageNumber - 1].forEach {
            val requester = it.userData as? Member

            if (requester == null) {
                Sentry.capture("User data for requested track $it should have been of type Member and wasn't.")
                LOGGER.error("User data for requested track $it should have been of type Member and wasn't, please report to creator.")
                return
            }

            embed.appendDescription("""
                
                `$itemNumber.` [${it.info.title}](${it.info.uri}) | `${it.duration.milliseconds.format()}`
                *Who put it on? ${requester.formatted} did!*
                
            """.trimIndent())
            itemNumber++
        }

        if (queue.size > 0) {
            val plural = if (queue.size == 1) "tune" else "tunes"
            embed.appendDescription("""

                **${queue.size} banging $plural ready to be played, ${queue.sumBy { it.duration }.milliseconds.format()} long**
            """.trimIndent())
        }

        embed.setFooter("Page $pageNumber/${queueAsPages.size}", member.user.avatarUrl)
        channel.sendMessage(embed.build()).queue()
    }

    companion object {

        const val RGB_MAX_VALUE = 16777216

        private val LOGGER = logger<QueueCommand>()
    }
}