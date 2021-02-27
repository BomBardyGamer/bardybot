package me.bardy.bot.commands

import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandOptions
import me.bardy.bot.services.TrackService
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component

@Component
class LoopCommand(private val trackService: TrackService) : Command("loop") {

    override val options = CommandOptions(listOf("l", "repeat"), true)

    override suspend fun execute(message: Message, channel: TextChannel, arguments: List<String>) {
        val scheduler = trackService.getMusicManager(message.textChannel.guild.id).scheduler

        channel.sendMessage(if (scheduler.isLooping) {
            "**Loop's off boss, I'll stop looping now!**"
        } else {
            "**Loop's on boss, gonna be looping this corker of a tune until you tell me to stop!**"
        }).queue()

        scheduler.isLooping = !scheduler.isLooping
    }
}