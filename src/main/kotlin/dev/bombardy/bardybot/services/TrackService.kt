package dev.bombardy.bardybot.services

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.bombardy.bardybot.audio.Result
import dev.bombardy.bardybot.audio.MusicManager
import dev.bombardy.bardybot.audio.LoadResultHandler
import dev.bombardy.bardybot.getBean
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
        private val beanFactory: BeanFactory
) {

    private val musicManager = beanFactory.getBean<MusicManager>()

    var isPaused = false
        set(paused) {
            musicManager.player.isPaused = paused
            field = paused
        }

    var volume = 0
        set(volume) {
            musicManager.player.volume = volume
            field = volume
        }

    fun loadTrack(channel: TextChannel, track: List<String>, requester: Member): Result {
        val playerManager = beanFactory.getBean<AudioPlayerManager>()
        requester.guild.audioManager.sendingHandler = musicManager.sendHandler

        val trackString = track.joinToString(" ")

        val trackURL = when (URL_REGEX.matches(trackString)) {
            true -> trackString
            else -> "ytsearch:$trackString"
        }

        if (requester.guild.voiceChannels.isEmpty()) return Result.CANNOT_JOIN_CHANNEL

        val channelId = cacheService.getVoiceChannel(requester.guild.idLong)
        val voiceChannel = requester.voiceState?.channel

        when (evalResult(requester.guild.idLong, channelId, voiceChannel)) {
            Result.SUCCESSFUL -> connectionService.join(requireNotNull(voiceChannel))
            else -> return evalResult(requester.guild.idLong, channelId, voiceChannel)
        }

        playerManager.loadItemOrdered(musicManager, trackURL, LoadResultHandler(channel, requester, trackURL, this))

        return Result.SUCCESSFUL
    }

    fun playTrack(audioTrack: AudioTrack) {
        musicManager.scheduler.queue(audioTrack)
    }

    fun queueTracks(tracks: List<AudioTrack>, requester: Member) = tracks.forEach {
        musicManager.scheduler.queue(it)
        it.userData = requester
    }

    fun skipTrack() = musicManager.scheduler.nextTrack()

    fun playOrPause() {
        isPaused = !isPaused
    }

    companion object {
        private val URL_REGEX = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
    }

    fun evalResult(guildId: Long, channelId: Long?, voiceChannel: VoiceChannel?): Result {
        if (channelId == null) {
            if (voiceChannel == null) return Result.USER_NOT_IN_CHANNEL
            cacheService.putVoiceChannel(guildId, voiceChannel.idLong)

            if (channelId != voiceChannel.idLong) return Result.USER_NOT_IN_CHANNEL_WITH_BOT
        }

        if (voiceChannel == null) return Result.USER_NOT_IN_CHANNEL_WITH_BOT
        if (channelId != voiceChannel.idLong) return Result.USER_NOT_IN_CHANNEL_WITH_BOT

        return Result.SUCCESSFUL
    }
}