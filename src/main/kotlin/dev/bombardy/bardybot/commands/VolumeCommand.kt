package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.services.TrackService
import dev.bombardy.octo.command.Command
import net.dv8tion.jda.api.entities.Message
import kotlin.math.max
import kotlin.math.min

class VolumeCommand(private val trackService: TrackService) : Command(listOf("volume", "vol")) {

    override suspend fun execute(message: Message, arguments: List<String>) {
        val channel = message.channel
        val audioPlayer = trackService.getMusicManager(message.guild.id).player

        val volume = runCatching {
            min(200, max(0, arguments[0].toInt()))
        }.getOrElse {
            val match = PERCENTAGE_REGEX.find(arguments[0])
            val number = match?.groups?.get(1)?.value?.toInt()

            if (number != null) {
                return@getOrElse min(200, max(0, number))
            }

            return channel.sendMessage("**Computers may seem like magic, but even we can't convert letters to numbers!**").queue()
        }

        when (volume > audioPlayer.volume) {
            true -> channel.sendMessage("**I've bumped up the volume to $volume!**").queue()
            else -> channel.sendMessage("**I've turned the volume down to $volume!**").queue()
        }

        audioPlayer.volume = volume
    }

    companion object {
        private val PERCENTAGE_REGEX = "(\\d+)%".toRegex()
    }
}