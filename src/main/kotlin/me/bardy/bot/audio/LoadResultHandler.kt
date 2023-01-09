package me.bardy.bot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.GuildMessageChannel
import net.dv8tion.jda.api.entities.Member

/**
 * Represents the result of an attempt to load an audio track from a given URL, search query or
 * playlist, used to process the result of an attempt to load a given track by the Guild's [TrackManager].
 */
class LoadResultHandler(
    private val channel: GuildMessageChannel,
    private val requester: Member,
    private val trackURL: String,
    private val trackManager: TrackManager
) : AudioLoadResultHandler {

    /**
     * Will be called when a track fails to load from the [AudioSourceManager],
     * usually when an outbound connection cannot be established.
     */
    override fun loadFailed(exception: FriendlyException) {
        channel.sendMessage("Sorry, I tried my best, but I couldn't play what you requested because: ${exception.message}").queue()
    }

    /**
     * Will be called when a track is found by the [AudioSourceManager] and will
     * set the loaded track's user data to the member who requested it (provided
     * in the constructor of this class).
     */
    override fun trackLoaded(track: AudioTrack) {
        channel.sendMessage("I found this banging tune `${track.info.title}` and queued it up to be played!").queue()
        track.userData = requester
        trackManager.cacheItem(trackURL, track)
        trackManager.playTrack(channel.guild, track)
    }

    /**
     * Will be called when a track cannot be found from the [AudioSourceManager],
     * either because the provided link did not resolve to an audio track or playlist,
     * the provided search query returned no results, or the source manager which
     * the link would be processed using (e.g. YouTube, SoundCloud) is disabled and so
     * does not exist.
     */
    override fun noMatches() {
        val request = trackURL.removePrefix(SEARCH_PREFIX)
        channel.sendMessage("Sorry, I tried very hard, but I couldn't find any results for '$request'").queue()
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
        trackManager.cacheItem(trackURL, playlist)
        trackManager.playTrack(channel.guild, firstTrack)

        val message = if (playlist.isSearchResult) {
            val request = trackURL.removePrefix(SEARCH_PREFIX)
            "*What you asked for:* '$request'"
        } else {
            trackManager.queueTracks(playlist.tracks, requester)
            "*The first in the playlist* '${playlist.name}'"
        }
        channel.sendMessage("I found this banging tune `${firstTrack.info.title}` ($message) and queued it up to be played!").queue()
    }

    companion object {

        private const val SEARCH_PREFIX = "ytsearch:"
    }
}
