package me.bardy.bot.services

import lavalink.client.io.jda.JdaLavalink
import me.bardy.bot.audio.Result
import me.bardy.bot.logger
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import org.springframework.stereotype.Service

/**
 * Handles connection to a voice channel, used by [TrackService] to join the
 * bot to a voice channel in order to play music.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Service
class ConnectionService(private val lavalink: JdaLavalink) {

    /**
     * Connects the bot to the given voice channel, or, if the bot is
     * already in a channel, does nothing.
     */
//    fun join(channel: VoiceChannel) = runCatching {
//        lavalink.getLink(channel.guild.id).connect(channel)
//        LOGGER.debug("Successfully connected to voice channel $channel in guild ${channel.guild}")
//
//        Result.SUCCESSFUL
//    }.getOrElse {
//        if (it is InsufficientPermissionException) return Result.NO_PERMISSION_TO_JOIN
//        Result.OTHER
//    }

    fun join(channel: VoiceChannel) = try {
        lavalink.getLink(channel.guild.id).connect(channel)
        LOGGER.debug("Successfully connected to voice channel $channel in guild ${channel.guild}")

        Result.SUCCESSFUL
    } catch (exception: Exception) {
        if (exception is InsufficientPermissionException) Result.NO_PERMISSION_TO_JOIN
        Result.OTHER
    }

    /**
     * Disconnects the bot from the voice channel it's currently in, and will stop
     * the currently playing track and clear the queue if [clearQueue] is true, or,
     * if the bot is not in a channel, does nothing.
     *
     * //@param clearQueue if the queue should be cleared when the bot leaves the channel
     */
//    fun leave(guildId: String, clearQueue: Boolean = true) {
//        val link = lavalink.getLink(guildId)
//        if (clearQueue) link.resetPlayer()
//
//        link.disconnect()
//        LOGGER.debug("Successfully disconnected from voice channel")
//    }

    fun leave(guildId: String, clearQueue: Boolean = true) {
        val link = lavalink.getLink(guildId)
        if (clearQueue) link.resetPlayer()

        link.destroy()
        LOGGER.debug("Successfully disconnected from voice channel")
    }

    fun evalJoinResult(guildId: String, channelId: VoiceChannel?, voiceChannel: VoiceChannel?): Result {
        if (channelId == null) {
            if (voiceChannel == null) return Result.USER_NOT_IN_CHANNEL
            return Result.SUCCESSFUL
        }

        if (voiceChannel == null) return Result.USER_NOT_IN_CHANNEL_WITH_BOT
        if (channelId.id != voiceChannel.id) return Result.USER_NOT_IN_CHANNEL_WITH_BOT

        return Result.SUCCESSFUL
    }

    companion object {

        private val LOGGER = logger<ConnectionService>()
    }
}