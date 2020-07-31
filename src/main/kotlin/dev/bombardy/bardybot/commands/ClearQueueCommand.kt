package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.services.TrackService
import dev.bombardy.octo.command.Command
import net.dv8tion.jda.api.entities.Message

class ClearQueueCommand(
        private val trackService: TrackService
) : Command(listOf("clear", "cl", "cq"), true) {

    override suspend fun execute(message: Message, arguments: List<String>) {
        val channel = message.channel
        val queue = trackService.getMusicManager(message.guild.id).scheduler.queue

        if (queue.isEmpty()) {
            channel.sendMessage("I tried to clear the queue, but there wasn't any tracks to clear!").queue()
            return
        }

        queue.clear()
        channel.sendMessage("Bang! And the tracks are gone!").queue()
    }
}