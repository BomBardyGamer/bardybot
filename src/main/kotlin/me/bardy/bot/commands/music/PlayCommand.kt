package me.bardy.bot.commands.music

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import me.bardy.bot.command.argument
import me.bardy.bot.command.literal
import me.bardy.bot.command.runs
import me.bardy.bot.components.ManagerMap
import me.bardy.bot.services.TrackService
import me.bardy.bot.util.description
import me.bardy.bot.util.embed
import me.bardy.bot.util.logger
import net.dv8tion.jda.api.entities.GuildMessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
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

    val helpMessage: MessageEmbed = embed {
        description("""
            You got it wrong, here's how you use it:

            !play [Link or query]
        """.trimIndent())
    }

    override fun register(): LiteralCommandNode<CommandContext> = literal<CommandContext>("play") {
        argument("track", StringArgumentType.greedyString()) {
            runs {
                val member = it.source.member ?: return@runs
                val channel = it.source.channel as? GuildMessageChannel ?: return@runs
                val audioPlayer = musicManagers.get(it.source.guild.id).player
                if (audioPlayer.isPaused) it.source.reply("Just to remind you, I'm still on pause from earlier")
                trackService.loadTrack(channel, it.input.split(" ").drop(1).joinToString(" "), member).handle(it.source.channel)
            }
        }
        runs {
            val audioPlayer = musicManagers.get(it.source.guild.id).player
            if (audioPlayer.isPaused) {
                LOGGER.debug("Attempting to resume track ${audioPlayer.playingTrack}")
                audioPlayer.isPaused = false
                it.source.reply("About time you played that again! I was starting to get bored!")
                return@runs
            }
            it.source.reply(helpMessage)
        }
    }.build()

    companion object {

        private val LOGGER = logger<PlayCommand>()
    }
}
