package dev.bombardy.bardybot.spring

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.bombardy.bardybot.audio.MusicManager
import dev.bombardy.bardybot.audio.ResultHandler
import dev.bombardy.bardybot.getBean
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
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
class TrackService @Autowired constructor(private val connectionService: ConnectionService,
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

    fun loadTrack(channel: TextChannel, track: Array<String>, requester: Member): Boolean {
        val playerManager = beanFactory.getBean<AudioPlayerManager>()
        requester.guild.audioManager.sendingHandler = musicManager.sendHandler

        val voiceChannel = requester.voiceState?.channel ?: return false

        val trackString = track.joinToString(" ")

        val trackURL = when (URL_REGEX.matches(trackString)) {
            true -> trackString
            else -> "ytsearch:$trackString"
        }

        connectionService.join(voiceChannel)
        playerManager.loadItemOrdered(musicManager, trackURL, ResultHandler(channel, requester, trackURL, this))
        return true
    }

    fun playTrack(audioTrack: AudioTrack) {
        musicManager.scheduler.queue(audioTrack)
    }

    fun queueTracks(tracks: List<AudioTrack>, requester: Member) = tracks.forEach {
        musicManager.scheduler.queue(it)
        it.userData = requester
    }

    fun skipTrack() = musicManager.scheduler.nextTrack()

    companion object {
        private val URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
    }
}