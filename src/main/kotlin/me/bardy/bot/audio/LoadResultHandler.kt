package me.bardy.bot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import me.bardy.bot.services.TrackService
import net.dv8tion.jda.api.entities.GuildMessageChannel
import net.dv8tion.jda.api.entities.Member

/**
 * Represents the result of an attempt to load an audio track from a given URL, search query or
 * playlist, used to process the result of an attempt to load a given track by the Guild's [TrackService].
 */
class LoadResultHandler(
    private val channel: GuildMessageChannel,
    private val requester: Member,
    private val trackURL: String,
    private val trackService: TrackService
) : AudioLoadResultHandler {

    /**
     * Will be called when a track fails to load from the [AudioSourceManager],
     * usually when an outbound connection cannot be established.
     */
    override fun loadFailed(exception: FriendlyException) {
        channel.sendMessage("**I did my best, but couldn't play what you requested for this reason:** ${exception.message}").queue()
    }

    /**
     * Will be called when a track is found by the [AudioSourceManager] and will
     * set the loaded track's user data to the member who requested it (provided
     * in the constructor of this class).
     */
    override fun trackLoaded(track: AudioTrack) {
        channel.sendMessage("**I found this banging tune** `${track.info.title}` **and queued it up to be played!**").queue()
        track.userData = requester
        trackService.cacheItem(trackURL, track)
        trackService.playTrack(channel.guild, track)
    }

    /**
     * Will be called when a track cannot be found from the [AudioSourceManager],
     * either because the provided link did not resolve to an audio track or playlist,
     * the provided search query returned no results, or the source manager which
     * the link would be processed using (e.g. YouTube, SoundCloud) is disabled and so
     * does not exist.
     */
    override fun noMatches() {
        channel.sendMessage("**I tried very hard, but couldn't find any results for** \"${trackURL.removePrefix(SEARCH_PREFIX)}\"").queue()
    }

    /**
     * Will be called when a playlist or search result is found by the [AudioSourceManager],
     * and will play the first track of either the playlist or search result, followed by
     * adding all remaining tracks to the queue if the result is not that of a search query.
     *
     * Search results will be loaded as a playlist of results, and so will be called by
     * this method.
     */
    override fun playlistLoaded(playlist: AudioPlaylist) {
        val firstTrack = playlist.selectedTrack ?: playlist.tracks.first()
        firstTrack.userData = requester
        trackService.cacheItem(trackURL, playlist)
        trackService.playTrack(channel.guild, firstTrack)

        val message = if (playlist.isSearchResult) {
            "*Your original request:* \"${trackURL.removePrefix(SEARCH_PREFIX)}\""
        } else {
            trackService.queueTracks(playlist.tracks, requester)
            "*The first banging tune in the playlist* `${playlist.name}`"
        }
        channel.sendMessage("**I found this banging tune** `${firstTrack.info.title}` ($message) **and queued it up to be played!**").queue()
    }

    companion object {

        private const val SEARCH_PREFIX = "ytsearch:"
    }
}
