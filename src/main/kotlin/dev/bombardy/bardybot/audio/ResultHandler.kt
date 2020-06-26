package dev.bombardy.bardybot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.bombardy.bardybot.spring.TrackService
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel

class ResultHandler(private val channel: TextChannel,
                    private val requester: Member,
                    private val trackURL: String,
                    private val trackService: TrackService
) : AudioLoadResultHandler {

    override fun loadFailed(exception: FriendlyException) = channel.sendMessage("Could not play: ${exception.message}").queue()

    override fun trackLoaded(track: AudioTrack) {
        channel.sendMessage("**I've queued up** `${track.info.title}` **ready to be played.**").queue()
        track.userData = requester
        trackService.playTrack(track)
    }

    override fun noMatches() {
        val message = when (trackURL.contains("ytsearch:")) {
            true -> trackURL.substring(9)
            else -> trackURL
        }
        channel.sendMessage("**I couldn't find anything under** \"$message\"").queue()
    }

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