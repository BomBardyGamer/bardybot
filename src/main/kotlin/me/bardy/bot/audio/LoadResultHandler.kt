package me.bardy.bot.audio

import dev.arbjerg.lavalink.client.player.LavalinkLoadResult
import dev.arbjerg.lavalink.client.player.LoadFailed
import dev.arbjerg.lavalink.client.player.NoMatches
import dev.arbjerg.lavalink.client.player.PlaylistLoaded
import dev.arbjerg.lavalink.client.player.SearchResult
import dev.arbjerg.lavalink.client.player.Track
import dev.arbjerg.lavalink.client.player.TrackException
import dev.arbjerg.lavalink.client.player.TrackLoaded
import dev.arbjerg.lavalink.protocol.v4.PlaylistInfo
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import java.util.function.Consumer

/**
 * Represents the result of an attempt to load an audio track from a given URL, search query or
 * playlist, used to process the result of an attempt to load a given track by the Guild's [TrackManager].
 */
class LoadResultHandler(
    private val channel: GuildMessageChannel,
    private val requester: Member,
    private val trackURL: String,
    private val trackManager: TrackManager
) : Consumer<LavalinkLoadResult> {

    override fun accept(result: LavalinkLoadResult) {
        when (result) {
            is TrackLoaded -> loadTrack(result.track)
            is PlaylistLoaded -> loadPlaylist(Playlist(result.info, result.tracks))
            is NoMatches -> noMatches()
            is SearchResult -> searchResultLoaded(result)
            is LoadFailed -> loadFailed(result.exception)
        }
    }

    fun load(item: AudioItem) {
        when (item) {
            is AudioTrack -> loadTrack(item.track)
            is AudioPlaylist -> loadPlaylist(Playlist(item.info, item.tracks))
        }
    }

    /**
     * Will be called when a track fails to load from the [AudioSourceManager],
     * usually when an outbound connection cannot be established.
     */
    private fun loadFailed(exception: TrackException) {
        channel.sendMessage("Sorry, I tried my best, but I couldn't play what you requested because: ${exception.message}").queue()
    }

    /**
     * Will be called when a track is found by the [AudioSourceManager] and will
     * set the loaded track's user data to the member who requested it (provided
     * in the constructor of this class).
     */
    private fun loadTrack(track: Track) {
        channel.sendMessage("I found this banging tune `${track.info.title}` and queued it up to be played!").queue()
        val audioTrack = AudioTrack(track, requester)
        trackManager.cacheItem(trackURL, audioTrack)
        trackManager.playTrack(channel.guild, audioTrack)
    }

    /**
     * Will be called when a track cannot be found from the [AudioSourceManager],
     * either because the provided link did not resolve to an audio track or playlist,
     * the provided search query returned no results, or the source manager which
     * the link would be processed using (e.g. YouTube, SoundCloud) is disabled and so
     * does not exist.
     */
    private fun noMatches() {
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
    private fun loadPlaylist(playlist: Playlist) {
        loadTrackList(playlist.tracks, "*The first in the playlist* '${playlist.info.name}'")
        trackManager.queueTracks(playlist.tracks, requester)
    }

    private fun searchResultLoaded(data: SearchResult) {
        val request = trackURL.removePrefix(SEARCH_PREFIX)
        loadTrackList(data.tracks, "*What you asked for:* '$request'")
    }

    private fun loadTrackList(tracks: List<Track>, loadMessage: String) {
        val firstTrack = tracks.firstOrNull() ?: return
        val firstAudioTrack = AudioTrack(firstTrack, requester)
        trackManager.cacheItem(trackURL, firstAudioTrack)
        trackManager.playTrack(channel.guild, firstAudioTrack)
        channel.sendMessage("I found this banging tune `${firstTrack.info.title}` ($loadMessage) and queued it up to be played!").queue()
    }

    private class Playlist(val info: PlaylistInfo, val tracks: List<Track>)

    companion object {

        private const val SEARCH_PREFIX = "ytsearch:"
    }
}
