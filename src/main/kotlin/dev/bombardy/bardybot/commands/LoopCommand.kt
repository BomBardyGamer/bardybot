package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.services.TrackService
import dev.bombardy.octo.command.Command
import net.dv8tion.jda.api.entities.Message

class LoopCommand(private val trackService: TrackService) : Command(listOf("loop", "l", "repeat"), true) {

    override suspend fun execute(message: Message, arguments: List<String>) {
        val channel = message.textChannel
        val scheduler = trackService.getMusicManager(message.textChannel.guild.id).scheduler

        when (scheduler.isLooping) {
            false -> channel.sendMessage("**Loop's on boss, gonna be looping this corker of a tune until you tell me to stop!**").queue()
            else -> channel.sendMessage("**Loop's off boss, I'll stop looping now!**").queue()
        }

        scheduler.isLooping = !scheduler.isLooping
    }
}