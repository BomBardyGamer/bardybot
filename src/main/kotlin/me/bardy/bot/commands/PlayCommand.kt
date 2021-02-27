package me.bardy.bot.commands

import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandMessages
import me.bardy.bot.command.CommandOptions
import me.bardy.bot.config.BotConfig
import me.bardy.bot.dsl.description
import me.bardy.bot.dsl.embed
import me.bardy.bot.logger
import me.bardy.bot.services.TrackService
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component

/**
 * Handles command execution for the play command, which plays music through the bot.
 *
 * @author BomBardyGamer
 * @since 1.0
 */
@Component
class PlayCommand(
    private val trackService: TrackService,
    config: BotConfig
) : Command("play") {

    override val options = CommandOptions(listOf("p"), true)

    override val messages = CommandMessages(
        help = MessageBuilder().setEmbed(embed {
            description = """
                **You got it wrong, here's how you use it:**
                
                ${config.prefix}play [Link or query]
            """.trimIndent()
        }).build()
    )

//    override val helpMessage = MessageBuilder().setEmbed(EmbedBuilder()
//            .setDescription("""
//                **You got it wrong, here's how you use it:**
//
//                ${prefix}play [Link or query]
//            """.trimIndent())
//            .setColor(Color.RED)
//            .build()
//    ).build()

    override suspend fun execute(message: Message, channel: TextChannel, arguments: List<String>) {
        val member = message.member ?: return
        val audioPlayer = trackService.getMusicManager(channel.guild.id).player

        if (arguments.isEmpty()) {
            if (audioPlayer.isPaused) {
                LOGGER.debug("Attempting to resume paused track ${audioPlayer.playingTrack}")
                audioPlayer.isPaused = false

                channel.sendMessage("**About time you played that again, I was starting to get bored!**").queue()
                return
            }
            channel.sendMessage(messages.help).queue()
            return
        }

        if (audioPlayer.isPaused) {
            channel.sendMessage("**Just to let you know, I'm still on pause from earlier!**").queue()
        }

        LOGGER.debug("Delegating track load to TrackService. Provided arguments: channel - $channel, arguments - $arguments, requester - $member")
        trackService.loadTrack(channel, arguments.joinToString(" "), member).handle(channel)
    }

    companion object {

        private val LOGGER = logger<PlayCommand>()
    }
}