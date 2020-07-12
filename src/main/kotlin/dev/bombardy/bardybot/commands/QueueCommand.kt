package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.*
import dev.bombardy.bardybot.services.TrackService
import dev.bombardy.octo.command.Command
import io.sentry.Sentry
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import java.util.concurrent.ArrayBlockingQueue
import kotlin.random.Random
import kotlin.time.milliseconds

class QueueCommand(private val trackService: TrackService) : Command(listOf("queue", "q"), true) {

    override suspend fun execute(message: Message, arguments: List<String>) {
        val musicManager = trackService.getMusicManager(message.textChannel.guild.id)

        val member = message.member ?: return
        val channel = message.channel
        val queue = musicManager.scheduler.queue

        val queueAsPages = queue.chunked(10)
        val embed = EmbedBuilder()
                .setTitle("Here's what I'm playing in ${message.guild.name}", "https://bot.bardy.me/")
                .setColor(Random.Default.nextInt(16777216))

        val pageNumber = runCatching {
            arguments.getOrElse(0) { "1" }.toInt()
        }.getOrElse {
            channel.sendMessage("**I can't lookup queue results by page number if you don't give me a number!**").queue()
            return
        }

        if (pageNumber == 1) {
            embed.setDescription("""

            __What's playing now:__

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
                    *Requested by: ${requester.formatName()}*

                """.trimIndent())
            }

            if (queueAsPages.isEmpty()) {
                embed.setFooter("Page 1/1", member.user.avatarUrl)

                channel.sendMessage(embed.build()).queue()
                return
            }
        }

        embed.appendDescription("""
            
            __What's next:__
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
                *Requested by: ${requester.formatName()}*
                
            """.trimIndent())
            itemNumber++
        }

        if (queue.size > 0) {
            val plural = when (queue.size == 1) {
                true -> "tune"
                else -> "tunes"
            }

            embed.appendDescription("""

                **${queue.size} $plural ready to be played, ${queue.sumBy { it.duration }.milliseconds.format()} long**
            """.trimIndent())
        }

        embed.setFooter("Page $pageNumber/${queueAsPages.size}", member.user.avatarUrl)
        channel.sendMessage(embed.build()).queue()
    }

    companion object {
        private val LOGGER = getLogger<QueueCommand>()
    }
}