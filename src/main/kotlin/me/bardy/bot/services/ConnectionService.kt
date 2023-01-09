package me.bardy.bot.services

import lavalink.client.io.jda.JdaLavalink
import me.bardy.bot.audio.JoinResult
import org.apache.logging.log4j.LogManager
import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.Guild
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
    fun tryJoin(channel: VoiceChannel): JoinResult {
        val botVoiceChannel = getBotVoiceChannel(channel.guild)
        val joinResult = canJoin(botVoiceChannel, channel)
        if (joinResult != JoinResult.SUCCESSFUL) return joinResult
        return joinChannel(channel)
    }

    private fun joinChannel(channel: VoiceChannel): JoinResult {
        return try {
            lavalink.getLink(channel.guild.id).connect(channel)
            LOGGER.debug("Successfully connected to voice channel $channel in guild ${channel.guild}")
            JoinResult.SUCCESSFUL
        } catch (_: InsufficientPermissionException) {
            JoinResult.NO_PERMISSION_TO_JOIN
        } catch (_: Exception) {
            JoinResult.OTHER
        }
    }

    /**
     * Disconnects the bot from the voice channel it's currently in, and will stop
     * the currently playing track and clear the queue if [clearQueue] is true, or,
     * if the bot is not in a channel, does nothing.
     */
    fun leave(guildId: String, clearQueue: Boolean = true) {
        val link = lavalink.getLink(guildId)
        if (clearQueue) link.resetPlayer()
        link.destroy()
        LOGGER.debug("Successfully disconnected from voice channel")
    }

    private fun canJoin(botChannel: AudioChannel?, userChannel: AudioChannel): JoinResult {
        return when {
            botChannel == null -> JoinResult.SUCCESSFUL
            botChannel.id != userChannel.id -> JoinResult.USER_NOT_IN_CHANNEL_WITH_BOT
            else -> JoinResult.SUCCESSFUL
        }
    }

    private fun getBotVoiceChannel(guild: Guild): AudioChannel? = guild.selfMember.voiceState?.channel

    companion object {

        private val LOGGER = LogManager.getLogger()
    }
}
