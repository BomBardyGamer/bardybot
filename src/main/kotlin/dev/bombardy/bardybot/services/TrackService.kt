package dev.bombardy.bardybot.services

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.bombardy.bardybot.audio.LoadResultHandler
import dev.bombardy.bardybot.audio.MusicManager
import dev.bombardy.bardybot.audio.Result
import dev.bombardy.bardybot.getBean
import dev.bombardy.bardybot.getLogger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Handles loading, playing, pausing, skipping, queueing, and volume of tracks.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Service
class TrackService @Autowired constructor(
        private val connectionService: ConnectionService,
        private val cacheService: RedisCacheService,
        private val jda: JDA,
        private val beanFactory: BeanFactory
) {

    val musicManagers = mutableMapOf<Long, MusicManager>()

    @Synchronized
    fun getMusicManager(guildId: Long): MusicManager {
        val musicManager = musicManagers.getOrPut(guildId, { beanFactory.getBean() })
        requireNotNull(jda.getGuildById(guildId)).audioManager.sendingHandler = musicManager.sendHandler
        return musicManager
    }

    fun loadTrack(channel: TextChannel, track: List<String>, requester: Member): Result {
        val musicManager = getMusicManager(channel.guild.idLong)
        val playerManager = beanFactory.getBean<AudioPlayerManager>()

        val trackString = track.joinToString(" ")

        val trackURL = when (URL_REGEX.matches(trackString)) {
            true -> trackString
            else -> "ytsearch:$trackString"
        }

        if (requester.guild.voiceChannels.isEmpty()) {
            LOGGER.debug("No voice channels found in guild.")
            return Result.CHANNELS_NOT_EXIST
        }

        val channelId = cacheService.getVoiceChannel(requester.guild.idLong)
        val voiceChannel = requester.voiceState?.channel

        val result = evalResult(requester.guild.idLong, channelId, voiceChannel)
        if (result != Result.SUCCESSFUL) return result

        val connectionResult = connectionService.join(requireNotNull(voiceChannel))
        if (connectionResult != Result.SUCCESSFUL) return connectionResult

        channel.sendMessage("**I'm performing a search for your query** `$trackString`").queue()
        playerManager.loadItemOrdered(musicManager, trackURL, LoadResultHandler(channel, requester, trackURL, this))

        return Result.SUCCESSFUL
    }

    fun playTrack(guildId: Long, audioTrack: AudioTrack) {
        val musicManager = getMusicManager(guildId)
        musicManager.scheduler.queue(audioTrack)
    }

    fun queueTracks(tracks: List<AudioTrack>, requester: Member) {
        val musicManager = getMusicManager(requester.guild.idLong)
        tracks.forEach {
            musicManager.scheduler.queue(it)
            it.userData = requester
        }
    }

    fun skipTrack(guildId: Long): Boolean {
        val musicManager = getMusicManager(guildId)
        return musicManager.scheduler.nextTrack()
    }

    fun evalResult(guildId: Long, channelId: Long?, voiceChannel: VoiceChannel?): Result {
        if (channelId == null) {
            if (voiceChannel == null) return Result.USER_NOT_IN_CHANNEL

            return Result.SUCCESSFUL
        }

        if (voiceChannel == null) return Result.USER_NOT_IN_CHANNEL_WITH_BOT
        if (channelId != voiceChannel.idLong) return Result.USER_NOT_IN_CHANNEL_WITH_BOT

        return Result.SUCCESSFUL
    }

    companion object {
        private val URL_REGEX = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()

        private val LOGGER = getLogger<TrackService>()
    }
}