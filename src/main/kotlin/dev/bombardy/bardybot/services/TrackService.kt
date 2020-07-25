package dev.bombardy.bardybot.services

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.bombardy.bardybot.audio.LoadResultHandler
import dev.bombardy.bardybot.audio.MusicManager
import dev.bombardy.bardybot.audio.Result
import dev.bombardy.bardybot.getLogger
import lavalink.client.io.jda.JdaLavalink
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import org.springframework.stereotype.Service

/**
 * Handles loading, playing, pausing, skipping, queueing, and volume of tracks.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Service
class TrackService(
        private val connectionService: ConnectionService,
        private val lavalink: JdaLavalink,
        private val playerManager: AudioPlayerManager
) {

    val musicManagers = mutableMapOf<String, MusicManager>()

    @Synchronized
    fun getMusicManager(guildId: String) = musicManagers.getOrPut(guildId, { MusicManager(lavalink.getLink(guildId)) })

    @Synchronized
    fun removeMusicManager(guildId: String) = musicManagers.remove(guildId)

    fun loadTrack(channel: TextChannel, track: String, requester: Member): Result {
        val musicManager = getMusicManager(channel.guild.id)

        val trackURL = when (URL_REGEX.matches(track)) {
            true -> track
            else -> "ytsearch:$track"
        }

        if (requester.guild.voiceChannels.isEmpty()) {
            LOGGER.debug("No voice channels found in guild.")
            return Result.CHANNELS_NOT_EXIST
        }

        val guildId = requester.guild.id
        val channelId = requester.guild.selfMember.voiceState?.channel
        val voiceChannel = requester.voiceState?.channel

        val result = evalResult(guildId, channelId, voiceChannel)
        if (result != Result.SUCCESSFUL) return result

        val connectionResult = connectionService.join(requireNotNull(voiceChannel))
        if (connectionResult != Result.SUCCESSFUL) return connectionResult

        channel.sendMessage("**I'm having a look around to see if I can find ** `$track`").queue()
        playerManager.loadItemOrdered(musicManager, trackURL, LoadResultHandler(channel, requester, trackURL, this))

        return Result.SUCCESSFUL
    }

    fun playTrack(guildId: String, audioTrack: AudioTrack) {
        val musicManager = getMusicManager(guildId)
        musicManager.scheduler.queue(audioTrack)
    }

    fun queueTracks(tracks: List<AudioTrack>, requester: Member) {
        val musicManager = getMusicManager(requester.guild.id)
        tracks.forEach {
            musicManager.scheduler.queue(it)
            it.userData = requester
        }
    }

    fun skipTrack(guildId: String): Boolean {
        val musicManager = getMusicManager(guildId)
        return musicManager.scheduler.nextTrack()
    }

    fun evalResult(guildId: String, channelId: VoiceChannel?, voiceChannel: VoiceChannel?): Result {
        if (channelId == null) {
            if (voiceChannel == null) return Result.USER_NOT_IN_CHANNEL

            return Result.SUCCESSFUL
        }

        if (voiceChannel == null) return Result.USER_NOT_IN_CHANNEL_WITH_BOT
        if (channelId.id != voiceChannel.id) return Result.USER_NOT_IN_CHANNEL_WITH_BOT

        return Result.SUCCESSFUL
    }

    companion object {
        private val URL_REGEX = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()

        private val LOGGER = getLogger<TrackService>()
    }
}