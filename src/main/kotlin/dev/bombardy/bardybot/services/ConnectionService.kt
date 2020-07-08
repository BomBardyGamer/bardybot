package dev.bombardy.bardybot.services

import dev.bombardy.bardybot.audio.Result
import dev.bombardy.bardybot.getLogger
import lavalink.client.io.jda.JdaLavalink
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Handles connection to a voice channel, used by [TrackService] to join the
 * bot to a voice channel in order to play music.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Service
class ConnectionService @Autowired constructor(
        private val jda: JDA,
        private val lavalink: JdaLavalink
) {

    /**
     * Connects the bot to the given voice channel, or, if the bot is
     * already in a channel, does nothing.
     */
    fun join(channel: VoiceChannel) = runCatching {
        lavalink.getLink(channel.guild.id).connect(channel)
        LOGGER.debug("Successfully connected to voice channel $channel in guild ${channel.guild}")

        Result.SUCCESSFUL
    }.getOrElse {
        if (it is InsufficientPermissionException) return Result.NO_PERMISSION_TO_JOIN
        Result.OTHER
    }

    /**
     * Disconnects the bot from the voice channel it's currently in, and will stop
     * the currently playing track and clear the queue if [clearQueue] is true, or,
     * if the bot is not in a channel, does nothing.
     *
     * //@param clearQueue if the queue should be cleared when the bot leaves the channel
     */
    fun leave(guildId: String, clearQueue: Boolean) {
        val link = lavalink.getLink(guildId)
        if (clearQueue) link.resetPlayer()

        link.disconnect()
        LOGGER.debug("Successfully disconnected from voice channel")
    }

    fun reconnect(guildId: String) {
        val link = lavalink.getLink(guildId)
        val voiceChannel = link.channel?.let { requireNotNull(jda.getGuildById(guildId)).getVoiceChannelById(it) }

        LOGGER.debug("Attempting to disconnect from voice channel $voiceChannel in guild with id $guildId")
        link.disconnect()

        LOGGER.debug("Attempting to reconnect to voice channel $voiceChannel in guild with id $guildId")
        runCatching {
            link.connect(voiceChannel)
            LOGGER.debug("Successfully reconnected to voice channel $voiceChannel in guild with id $guildId")
        }.getOrElse {
            LOGGER.debug("Unable to connect to voice channel $voiceChannel. ${it.message}")
        }
    }

    companion object {
        private val LOGGER = getLogger<ConnectionService>()
    }
}