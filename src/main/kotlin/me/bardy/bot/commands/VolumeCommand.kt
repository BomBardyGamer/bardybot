package me.bardy.bot.commands

import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandOptions
import me.bardy.bot.services.TrackService
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component
import kotlin.math.max
import kotlin.math.min

@Component
class VolumeCommand(private val trackService: TrackService) : Command("volume") {

    override val options = CommandOptions(listOf("vol"))

    override suspend fun execute(message: Message, channel: TextChannel, arguments: List<String>) {
        val audioPlayer = trackService.getMusicManager(message.guild.id).player
        val volumeBounds = Bounds(2.0f, 0.0f)

        val volume = try {
            forceBetween(arguments[0].toInt() / 100.0f, volumeBounds)
        } catch (exception: Exception) {
            val match = PERCENTAGE_REGEX.find(arguments[0])
            val number = match?.groups?.get(1)?.value?.toInt()?.div(100.0f)

            if (number != null) forceBetween(number, volumeBounds)
            return channel.sendMessage("**Computers may seem like magic, but even we can't convert letters to numbers!**").queue()
        }

        val action = if (volume > audioPlayer.filters.volume) "bumped up the volume" else "turned the volume down"
        channel.sendMessage("**I've $action to ${(volume * 100).toInt()}!**").queue()

        audioPlayer.filters.volume = volume
        audioPlayer.filters.commit()
    }

    private fun forceBetween(value: Float, bounds: Bounds) = min(bounds.upper, max(bounds.lower, value))

    data class Bounds(val upper: Float, val lower: Float)

    companion object {
        private val PERCENTAGE_REGEX = "(\\d+)%".toRegex()
    }
}