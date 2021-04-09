package me.bardy.bot.commands.music

import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.tree.LiteralCommandNode
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import me.bardy.bot.components.ManagerMap
import me.bardy.bot.format
import me.bardy.bot.formatted
import me.bardy.bot.logger
import me.bardy.bot.sumBy
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import org.springframework.stereotype.Component
import kotlin.random.Random
import kotlin.time.milliseconds

@Component
class QueueCommand(private val musicManagers: ManagerMap) : Command("q") {

    override fun register(): LiteralCommandNode<CommandContext> = literal<CommandContext>("queue")
        .then(argument<CommandContext, Int>("page", integer())
            .executes { paginateQueue(it.source, it.getArgument("page", Int::class.java)); 1 })
        .executes { paginateQueue(it.source); 1 }
        .build()

    private fun paginateQueue(context: CommandContext, pageNumber: Int = 1) {
        val member = context.member ?: return

        val musicManager = musicManagers[context.guild.id]
        val queue = musicManager.scheduler.queue

        val paginatedQueue = queue.chunked(10)
        val embed = EmbedBuilder()
            .setTitle("Here's what I've got lined up for you in ${context.guild.name}", "https://bot.bardy.me")
            .setColor(Random.nextInt(RGB_MAX_VALUE))

        if (pageNumber == 1) {
            embed.setDescription("\n\n__What I'm playing now:__\n")

            val nowPlaying = musicManager.player.playingTrack
            if (nowPlaying == null) {
                embed.appendDescription("You still haven't queued anything! Come on, let's get this party started! \uD83C\uDF89")
            } else {
                val requester = nowPlaying.userData as? Member
                if (requester == null) {
                    LOGGER.error("User data for requested track $nowPlaying was not of type Member.")
                    return
                }

                embed.appendTrack(nowPlaying, requester, -1)
            }

            if (paginatedQueue.isEmpty()) {
                context.reply(embed.setFooter("Page 1/1", member.user.avatarUrl).build())
                return
            }
        }

        embed.appendDescription("\n__What I've got for you next:__\n")

        paginatedQueue.forEach { page ->
            page.forEachIndexed { i, it ->
                val requester = it.userData as? Member
                if (requester == null) {
                    LOGGER.error("User data for requested track $it was not of type Member.")
                    return
                }

                embed.appendTrack(it, requester, i + 1)
            }
        }

        if (queue.size > 0) {
            val plural = if (queue.size == 1) "tune" else "tunes"
            val length = queue.sumBy { it.duration }.milliseconds.format()
            embed.appendDescription("\n**${queue.size} banging $plural ready to be played, $length long**")
        }

        context.reply(embed.setFooter("Page $pageNumber / ${paginatedQueue.size}", member.user.avatarUrl).build())
    }

    private fun EmbedBuilder.appendTrack(track: AudioTrack, requester: Member, number: Int) {
        if (number != -1) appendDescription("\n`$number`. ") else appendDescription("\n")
        appendDescription("""[${track.info.title}](${track.info.uri}) | `${track.duration.milliseconds.format()}`
            *Who put it on? ${requester.formatted} did!*

        """.trimIndent())
    }

    companion object {

        const val RGB_MAX_VALUE = 16777216

        private val LOGGER = logger<QueueCommand>()
    }
}