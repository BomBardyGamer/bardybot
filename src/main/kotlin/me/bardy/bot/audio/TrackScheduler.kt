package me.bardy.bot.audio

import dev.arbjerg.lavalink.client.event.TrackEndEvent
import dev.arbjerg.lavalink.client.player.LavalinkPlayer
import dev.arbjerg.lavalink.client.player.Track
import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason
import java.util.Queue
import java.util.concurrent.ArrayBlockingQueue
import org.apache.logging.log4j.LogManager
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Represents a Guild Track Scheduler, used for queueing, clearing, skipping,
 * and autoplaying the next track in the queue. Also, will be responsible for
 * looping tracks where necessary in the future, though that is not implemented
 * yet.
 */
class TrackScheduler(private val playerProvider: Supplier<LavalinkPlayer?>) : Consumer<TrackEndEvent> {

    private var looping = false
    private val queue = ArrayBlockingQueue<AudioTrack>(100)
    private var playingTrack: AudioTrack? = null

    fun queue(): Queue<AudioTrack> = queue

    fun playing(): AudioTrack? = playingTrack

    fun isLooping(): Boolean = looping

    fun startLooping() {
        looping = true
    }

    fun stopLooping() {
        looping = false
    }

    fun queue(track: AudioTrack) {
        LOGGER.debug("Attempting to start track {}, not interrupting currently playing track.", track)

        val player = playerProvider.get() ?: return
        if (player.track != null) {
            LOGGER.debug("Could not start track {} as there was a track already playing, adding to queue.", track)
            queue.offer(track)
        } else {
            LOGGER.debug("Successfully started track {} as no track was playing.", track)
            play(player, track)
        }
    }

    fun nextTrack(): Boolean {
        val player = playerProvider.get() ?: return false

        val next = queue.poll()
        if (next == null) {
            player.stopTrack()
            return false
        }

        play(player, next)
        return true
    }

    private fun play(player: LavalinkPlayer, track: AudioTrack) {
        playingTrack = track
        player.setTrack(track.track).subscribe()
    }

    override fun accept(event: TrackEndEvent) {
        val player = playerProvider.get() ?: return
        onTrackEnd(player, event.track, event.endReason)
    }

    private fun onTrackEnd(player: LavalinkPlayer, track: Track, endReason: AudioTrackEndReason) {
        LOGGER.debug("TrackEndEvent intercepted! Track {} has ended from player {} with reason {}", track, player, endReason)
        if (!endReason.mayStartNext) return

        LOGGER.debug("End reason was mayStartNext, attempting to start next track {}", queue.peek())
        if (looping) {
            LOGGER.debug("Loop is enabled. Attempting to start clone of previous track {} with player {}", track, player)
            player.setTrack(track.makeClone())
            return
        }

        // Don't care what happens if this doesn't succeed
        nextTrack()
    }

    companion object {

        private val LOGGER = LogManager.getLogger()
    }
}
