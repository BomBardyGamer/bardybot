package me.bardy.bot.commands.music

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.bardy.bot.audio.JoinResult
import me.bardy.bot.command.Command
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.command.argument
import me.bardy.bot.command.getArgument
import me.bardy.bot.command.literal
import me.bardy.bot.command.runs
import me.bardy.bot.config.bot.BotConfig
import me.bardy.bot.services.TrackService
import me.bardy.bot.util.description
import me.bardy.bot.util.embed
import me.bardy.bot.util.GuildMusicManagers
import net.dv8tion.jda.api.entities.GuildMessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component

/**
 * Handles command execution for the play command, which plays music through the bot.
 *
 * @author BomBardyGamer
 * @since 1.0
 */
@Component
class PlayCommand(
    private val botConfig: BotConfig,
    private val musicManagers: GuildMusicManagers,
    private val trackService: TrackService
) : Command(setOf("play")) {

    val helpMessage: MessageEmbed = embed {
        description("""
            You got it wrong, here's how you use it:

            ${botConfig.prefix}play [Link or query]
        """.trimIndent())
    }

    override fun create(): LiteralArgumentBuilder<BotCommandContext> = literal("play") {
        argument("track", StringArgumentType.greedyString()) {
            runs {
                val member = it.source.member ?: return@runs
                val channel = it.source.channel as? GuildMessageChannel ?: return@runs

                val manager = musicManagers.getByGuild(it.source.guild)
                if (manager.isPaused()) it.source.reply("Just to remind you, I'm still on pause from earlier")

                val track = it.getArgument<String>("track")
                val result = trackService.loadTrack(channel, track, member)
                if (result != JoinResult.SUCCESSFUL) it.source.channel.sendMessage(result.message).queue()
            }
        }
        runs {
            val manager = musicManagers.getByGuild(it.source.guild)
            if (manager.isPaused()) {
                LOGGER.debug("Attempting to resume track ${manager.playingTrack()}")
                manager.resume()
                it.source.reply("About time you played that again! I was starting to get bored!")
                return@runs
            }
            it.source.reply(helpMessage)
        }
    }

    companion object {

        private val LOGGER = LogManager.getLogger()
    }
}
