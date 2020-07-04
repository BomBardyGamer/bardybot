package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.services.TrackService
import dev.bombardy.octo.command.Command
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import java.awt.Color

/**
 * Handles command execution for the play command, which plays music through the bot.
 *
 * @author BomBardyGamer
 * @since 1.0
 */
class PlayCommand(private val trackService: TrackService) : Command(listOf("play", "p"), true) {

    override val helpMessage = MessageBuilder().setEmbed(EmbedBuilder()
            .setDescription("""
                **You got it wrong, here's how you use it:**

                ${prefix}play [Link or query]
            """.trimIndent())
            .setColor(Color.RED)
            .build()
    ).build()

    override fun execute(message: Message, arguments: List<String>) {
        val member = message.member ?: return

        if (arguments.isEmpty()) {
            if (trackService.isPaused) {
                trackService.isPaused = false
                return
            }
            message.channel.sendMessage(helpMessage).queue()
            return
        }

        message.channel.sendMessage(trackService.loadTrack(message.textChannel, arguments, member).message).queue()
    }
}