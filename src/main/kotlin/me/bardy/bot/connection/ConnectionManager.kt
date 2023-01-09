package me.bardy.bot.connection

import lavalink.client.io.jda.JdaLavalink
import me.bardy.bot.audio.JoinResult
import org.apache.logging.log4j.LogManager
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import org.springframework.stereotype.Service

@Service
class ConnectionManager(private val lavalink: JdaLavalink) {

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

    fun leave(guild: Guild) {
        val link = lavalink.getLink(guild.id)
        link.resetPlayer()
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
