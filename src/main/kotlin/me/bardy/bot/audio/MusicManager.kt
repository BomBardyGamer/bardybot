package me.bardy.bot.audio

import dev.arbjerg.lavalink.client.Link
import dev.arbjerg.lavalink.client.event.TrackEndEvent
import dev.arbjerg.lavalink.client.player.LavalinkPlayer
import dev.arbjerg.lavalink.client.player.PlayerUpdateBuilder

/**
 * The music manager for a guild.
 */
class MusicManager(private val link: Link) {

    private val scheduler = TrackScheduler(this::player)

    init {
        link.createOrUpdatePlayer().subscribe()
        link.node.on<TrackEndEvent>().subscribe(scheduler)
    }

    private fun player(): LavalinkPlayer? = link.cachedPlayer

    fun playingTrack(): AudioTrack? = scheduler.playing()

    fun trackPosition(): Long = player()?.position ?: 0L

    fun isPaused(): Boolean = player()?.paused == true

    fun pause() {
        updatePlayer { setPaused(true) }
    }

    fun resume() {
        updatePlayer { setPaused(false) }
    }

    fun volume(): Int {
        val player = player() ?: return 0
        return player.volume
    }

    fun setVolume(value: Int) {
        updatePlayer { setVolume(value) }
    }

    private inline fun updatePlayer(update: LavalinkPlayer.() -> PlayerUpdateBuilder) {
        val player = player() ?: return
        update(player).subscribe()
    }

    fun isLooping(): Boolean = scheduler.isLooping()

    fun startLooping() {
        scheduler.startLooping()
    }

    fun stopLooping() {
        scheduler.stopLooping()
    }

    fun queue(track: AudioTrack) {
        scheduler.queue(track)
    }

    fun nextTrack(): Boolean = scheduler.nextTrack()

    fun hasQueuedTracks(): Boolean = scheduler.queue().isNotEmpty()

    fun queuedTrackCount(): Int = scheduler.queue().size

    fun clearQueue() {
        scheduler.queue().clear()
    }

    fun paginateQueue(resultsPerPage: Int): List<List<AudioTrack>> = scheduler.queue().chunked(resultsPerPage)

    fun getTotalQueuedTime(): Long = scheduler.queue().sumOf { it.track.info.length }
}
