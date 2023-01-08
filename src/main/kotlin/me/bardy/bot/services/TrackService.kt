package me.bardy.bot.services

import com.github.benmanes.caffeine.cache.Caffeine
import com.sedmelluq.discord.lavaplayer.track.AudioItem
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.util.concurrent.TimeUnit
import lavalink.client.io.jda.JdaLavalink
import me.bardy.bot.audio.LoadResultHandler
import me.bardy.bot.audio.JoinResult
import me.bardy.bot.util.logger
import me.bardy.bot.util.GuildMusicManagers
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildMessageChannel
import net.dv8tion.jda.api.entities.Member
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
    private val musicManagers: GuildMusicManagers,
    private val lavalink: JdaLavalink
) {

    private val audioItemCache = Caffeine.newBuilder()
        .maximumSize(64)
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .build<String, AudioItem>()

    fun loadTrack(channel: GuildMessageChannel, track: String, requester: Member): JoinResult {
        val trackURL = if (URL_REGEX.matches(track)) track else "ytsearch:${track.lowercase()}"
        if (requester.guild.voiceChannels.isEmpty()) {
            LOGGER.debug("No voice channels found in guild.")
            return JoinResult.NO_CHANNELS
        }
        val channelId = requester.guild.selfMember.voiceState?.channel
        val voiceChannel = requester.voiceState?.channel as? VoiceChannel

        connectionService.evaluateJoin(channelId, voiceChannel).require(JoinResult.SUCCESSFUL) { return it }
        connectionService.join(requireNotNull(voiceChannel)).require(JoinResult.SUCCESSFUL) { return it }

        channel.sendMessage("**I'm having a look around to see if I can find ** `$track`").queue()
        if (audioItemCache.getIfPresent(trackURL) != null) {
            val resultHandler = LoadResultHandler(channel, requester, trackURL, this)
            when (val item = audioItemCache.getIfPresent(trackURL)!!) {
                is AudioTrack -> resultHandler.trackLoaded(item)
                is AudioPlaylist -> resultHandler.playlistLoaded(item)
            }

            return JoinResult.SUCCESSFUL
        }

        lavalink.getLink(channel.guild).restClient.loadItem(trackURL, LoadResultHandler(channel, requester, trackURL, this))
        return JoinResult.SUCCESSFUL
    }

    fun playTrack(guild: Guild, audioTrack: AudioTrack) {
        musicManagers.getByGuild(guild).scheduler.queue(audioTrack)
    }

    fun queueTracks(tracks: List<AudioTrack>, requester: Member) {
        val musicManager = musicManagers.getByGuild(requester.guild)
        tracks.forEach {
            musicManager.scheduler.queue(it)
            it.userData = requester
        }
    }

    fun skipTrack(guild: Guild): Boolean = musicManagers.getByGuild(guild).scheduler.nextTrack()

    fun cacheItem(trackURL: String, item: AudioItem) {
        audioItemCache.put(trackURL, item)
    }

    companion object {

        private val URL_REGEX = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
        private val LOGGER = logger<TrackService>()
    }
}
