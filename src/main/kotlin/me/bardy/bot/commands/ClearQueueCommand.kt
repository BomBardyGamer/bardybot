package me.bardy.bot.commands

import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandOptions
import me.bardy.bot.services.TrackService
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component

@Component
class ClearQueueCommand(private val trackService: TrackService) : Command("clear") {

    override val options = CommandOptions(listOf("cl", "cq"), true)

    override suspend fun execute(message: Message, channel: TextChannel, arguments: List<String>) {
        val queue = trackService.getMusicManager(message.guild.id).scheduler.queue

        if (queue.isEmpty()) {
            channel.sendMessage("I tried to clear the queue, but there wasn't any tracks to clear!").queue()
            return
        }

        queue.clear()
        channel.sendMessage("Bang! And the tracks are gone!").queue()
    }
}