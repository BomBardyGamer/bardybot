package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.spring.TrackService
import me.mattstudios.mfjda.annotations.Command
import me.mattstudios.mfjda.annotations.Default
import me.mattstudios.mfjda.base.CommandBase
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

@Command("play", "p")
class PlayCommand(private val trackService: TrackService,
                  private val prefix: String
) : CommandBase() {

    @Default
    fun defaultCommand(track: Array<String>) {
        val channel = message.textChannel

        val member = when (message.author.isBot) {
            true -> return channel.sendMessage("**Sorry mate, unfortunately bots can't use commands**").queue()
            else -> message.member ?: return
        }

        if (track.isEmpty()) {
            if (trackService.isPaused) {
                trackService.isPaused = false
                return
            }
            return channel.sendMessage(EmbedBuilder()
                    .setDescription("""
                        **You got it wrong, here's how you use it:**
                        
                        ${prefix}play [Link or query]
                    """.trimIndent())
                    .setColor(Color.RED)
                    .build()).queue()
        }

        if (!trackService.loadTrack(channel, track, member)) {
            channel.sendMessage("**I can't play music if there isn't a channel to play music on!**").queue()
            return
        }
    }
}