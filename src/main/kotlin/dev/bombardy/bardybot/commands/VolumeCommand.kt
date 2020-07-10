package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.services.TrackService
import dev.bombardy.octo.command.Command
import net.dv8tion.jda.api.entities.Message
import kotlin.math.min

class VolumeCommand(private val trackService: TrackService) : Command(listOf("volume", "vol")) {

    override suspend fun execute(message: Message, arguments: List<String>) {
        val audioPlayer = trackService.getMusicManager(message.guild.id).player
        val volume = arguments[0].toInt()

        audioPlayer.volume = min(200, volume)
        message.channel.sendMessage("**I've set the volume to $volume.**").queue()
    }
}