package dev.bombardy.bardybot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
import dev.bombardy.bardybot.services.TrackService
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel

/**
 * Represents the result of an attempt to load an audio track from a given URL, search query or
 * playlist, used to process the result of an attempt to load a given track by the Guild's [TrackService].
 *
 * @author Callum Seabrook
 * @since 1.0
 */
class LoadResultHandler(private val channel: TextChannel,
                        private val requester: Member,
                        private val trackURL: String,
                        private val trackService: TrackService
) : AudioLoadResultHandler {

    /**
     * Will be called when a track fails to load from the [AudioSourceManager],
     * usually when an outbound connection cannot be established.
     */
    override fun loadFailed(exception: FriendlyException) = channel.sendMessage("Could not play: ${exception.message}").queue()

    /**
     * Will be called when a track is found by the [AudioSourceManager] and will
     * set the loaded track's user data to the member who requested it (provided
     * in the constructor of this class).
     */
    override fun trackLoaded(track: AudioTrack) {
        channel.sendMessage("**I've queued up** `${track.info.title}` **ready to be played.**").queue()
        track.userData = requester
        trackService.playTrack(track)
    }

    /**
     * Will be called when a track cannot be found from the [AudioSourceManager],
     * either because the provided link did not resolve to an audio track or playlist,
     * the provided search query returned no results, or the source manager which
     * the link would be processed using (e.g. YouTube, SoundCloud, HTTP) is disabled
     * and so does not exist.
     */
    override fun noMatches() {
        val message = when (trackURL.contains("ytsearch:")) {
            true -> trackURL.substring(9)
            else -> trackURL
        }
        channel.sendMessage("**I couldn't find anything under** \"$message\"").queue()
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
        trackService.playTrack(firstTrack)

        val message = when (playlist.isSearchResult) {
            false -> {
                trackService.queueTracks(playlist.tracks, requester)
                "*The first track of the playlist:* `${playlist.name}`"
            }
            else -> "*What I found for:* \"${trackURL.substring(9)}\""
        }
        channel.sendMessage("**I've queued up** `${firstTrack.info.title}` ($message) **ready to be played.**").queue()
    }
}