package me.bardy.bot.audio

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.Queue
import java.util.concurrent.ArrayBlockingQueue
import lavalink.client.player.IPlayer
import lavalink.client.player.event.PlayerEventListenerAdapter
import org.apache.logging.log4j.LogManager

/**
 * Represents a Guild Track Scheduler, used for queueing, clearing, skipping,
 * and autoplaying the next track in the queue. Also, will be responsible for
 * looping tracks where necessary in the future, though that is not implemented
 * yet.
 */
class TrackScheduler(private val player: IPlayer) : PlayerEventListenerAdapter() {

    private var looping = false
    private val queue = ArrayBlockingQueue<AudioTrack>(100)

    fun queue(): Queue<AudioTrack> = queue

    fun isLooping(): Boolean = looping

    fun startLooping() {
        looping = true
    }

    fun stopLooping() {
        looping = false
    }

    fun queue(track: AudioTrack) {
        LOGGER.debug("Attempting to start track $track, not interrupting currently playing track.")

        if (player.playingTrack != null) {
            LOGGER.debug("Could not start track $track as there was a track already playing, adding to queue.")
            queue.offer(track)
        } else {
            LOGGER.debug("Successfully started track $track as no track was playing.")
            player.playTrack(track)
        }
    }

    fun nextTrack(): Boolean {
        val next = queue.poll()
        if (next == null) {
            player.stopTrack()
            return false
        }

        player.playTrack(next)
        return true
    }

    override fun onTrackEnd(player: IPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        LOGGER.debug("TrackEndEvent intercepted! Track $track has ended from player $player with reason $endReason")
        if (!endReason.mayStartNext) return

        LOGGER.debug("End reason was mayStartNext, attempting to start next track ${queue.peek()}")
        if (looping) {
            LOGGER.debug("Loop is enabled. Attempting to start clone of previous track $track with player $player")
            player.playTrack(track.makeClone())
            return
        }

        nextTrack()
    }

    companion object {

        private val LOGGER = LogManager.getLogger()
    }
}
