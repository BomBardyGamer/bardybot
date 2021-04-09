package me.bardy.bot.commands.music

import com.mojang.brigadier.arguments.StringArgumentType.greedyString
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.tree.LiteralCommandNode
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import me.bardy.bot.components.ManagerMap
import me.bardy.bot.dsl.description
import me.bardy.bot.dsl.embed
import me.bardy.bot.logger
import me.bardy.bot.services.TrackService
import org.springframework.stereotype.Component

/**
 * Handles command execution for the play command, which plays music through the bot.
 *
 * @author BomBardyGamer
 * @since 1.0
 */
@Component
class PlayCommand(
    private val musicManagers: ManagerMap,
    private val trackService: TrackService
) : Command("play") {

    val helpMessage = embed {
        description("""
            You got it wrong, here's how you use it:

            !play [Link or query]
        """.trimIndent())
    }

    override fun register(): LiteralCommandNode<CommandContext> = literal<CommandContext>("play")
        .then(argument<CommandContext, String>("track", greedyString())
            .executes {
                val member = it.source.member ?: return@executes 0
                val audioPlayer = musicManagers[it.source.guild.id].player
                if (audioPlayer.isPaused) it.source.reply("Just to remind you, I'm still on pause from earlier")

                trackService.loadTrack(
                    it.source.channel,
                    it.input.split(" ").drop(1).joinToString(" "),
                    member
                ).handle(it.source.channel)
                1
            })
        .executes {
            val audioPlayer = musicManagers[it.source.guild.id].player
            if (audioPlayer.isPaused) {
                LOGGER.debug("Attempting to resume track ${audioPlayer.playingTrack}")
                audioPlayer.isPaused = false

                it.source.reply("About time you played that again! I was starting to get bored!")
                return@executes 0
            }
            it.source.reply(helpMessage)
            1
        }
        .build()

    companion object {

        private val LOGGER = logger<PlayCommand>()
    }
}