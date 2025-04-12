package me.bardy.bot.audio

import com.github.benmanes.caffeine.cache.Caffeine
import dev.arbjerg.lavalink.client.LavalinkClient
import dev.arbjerg.lavalink.client.player.Track
import java.util.concurrent.TimeUnit
import me.bardy.bot.connection.ConnectionManager
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service

/**
 * Handles loading, playing, pausing, skipping, queueing, and volume of tracks.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Service
class TrackManager(
    private val connectionManager: ConnectionManager,
    private val musicManagers: GuildMusicManagers,
    private val lavalink: LavalinkClient
) {

    private val audioItemCache = Caffeine.newBuilder()
        .maximumSize(64)
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .build<String, AudioItem>()

    fun loadTrack(channel: GuildMessageChannel, track: String, requester: Member): JoinResult {
        val trackURL = if (URL_REGEX.matches(track)) track else "ytmsearch:${track.lowercase()}"
        if (requester.guild.voiceChannels.isEmpty()) {
            LOGGER.debug("No voice channels found in guild.")
            return JoinResult.NO_CHANNELS
        }
        val voiceChannel = requester.voiceState?.channel
        if (voiceChannel !is VoiceChannel) return JoinResult.USER_NOT_IN_CHANNEL

        val joinResult = connectionManager.tryJoin(voiceChannel)
        if (joinResult != JoinResult.SUCCESSFUL) return joinResult
        channel.sendMessage("I'm having a look around to see if I can find anything for `$track`").queue()

        val resultHandler = LoadResultHandler(channel, requester, trackURL, this)
        val cachedItem = audioItemCache.getIfPresent(trackURL)
        if (cachedItem != null) {
            resultHandler.load(cachedItem)
            return JoinResult.SUCCESSFUL
        }

        lavalink.getOrCreateLink(channel.guild.idLong).loadItem(trackURL).subscribe(resultHandler)
        return JoinResult.SUCCESSFUL
    }

    fun playTrack(guild: Guild, track: AudioTrack) {
        musicManagers.getByGuild(guild).queue(track)
    }

    fun queueTracks(tracks: List<Track>, requester: Member) {
        val musicManager = musicManagers.getByGuild(requester.guild)
        tracks.forEach { track -> musicManager.queue(AudioTrack(track, requester)) }
    }

    fun trySkipTrack(guild: Guild): Boolean = musicManagers.getByGuild(guild).nextTrack()

    fun cacheItem(trackURL: String, item: AudioItem) {
        audioItemCache.put(trackURL, item)
    }

    companion object {

        private val URL_REGEX = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
        private val LOGGER = LogManager.getLogger()
    }
}
