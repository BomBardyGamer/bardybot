package me.bardy.bot.audio

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import lavalink.client.io.Link
import lavalink.client.player.LavalinkPlayer

/**
 * The music manager for a guild.
 */
class MusicManager(link: Link) {

    val player: LavalinkPlayer = link.player
    val scheduler: TrackScheduler = TrackScheduler(player)

    init {
        player.addListener(scheduler)
    }

    fun playingTrack(): AudioTrack? = player.playingTrack

    fun trackPosition(): Long = player.trackPosition

    fun isPaused(): Boolean = player.isPaused

    fun pause() {
        player.isPaused = true
    }

    fun resume() {
        player.isPaused = false
    }

    fun volume(): Int = (player.filters.volume * 100F).toInt()

    fun setVolume(value: Int) {
        player.filters.volume = value.toFloat() / 100F
    }

    fun isLooping(): Boolean = scheduler.isLooping()

    fun startLooping() {
        scheduler.startLooping()
    }

    fun stopLooping() {
        scheduler.stopLooping()
    }

    fun addToQueue(track: AudioTrack) {
        scheduler.queue(track)
    }

    fun hasQueuedTracks(): Boolean = scheduler.queue().isNotEmpty()

    fun queuedTrackCount(): Int = scheduler.queue().size

    fun clearQueue() {
        scheduler.queue().clear()
    }

    fun paginateQueue(resultsPerPage: Int): List<List<AudioTrack>> = scheduler.queue().chunked(resultsPerPage)

    fun getTotalQueuedTime(): Long = scheduler.queue().sumOf { it.duration }
}
