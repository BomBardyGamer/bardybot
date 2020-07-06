package dev.bombardy.bardybot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import dev.bombardy.bardybot.getLogger
import java.util.concurrent.ArrayBlockingQueue

/**
 * Represents a Guild Track Scheduler, used for queueing, clearing, skipping,
 * and auto-playing the next track in the queue. Also will be responsible for
 * looping tracks where necessary in the future, though that is not implemented
 * yet.
 *
 * @author BomBardyGamer
 * @since 1.0
 */
class TrackScheduler(private val player: AudioPlayer) : AudioEventAdapter() {

    var isLooping = false

    val queue = ArrayBlockingQueue<AudioTrack>(100)

    fun queue(track: AudioTrack) {
        LOGGER.debug("Attempting to start track $track, not interrupting currently playing track.")

        if (!player.startTrack(track, true)) {
            LOGGER.debug("Could not start track $track as there was a track already playing, adding to queue.")
            queue.offer(track)
        }
    }

    fun clearQueue() = queue.clear()

    fun nextTrack(): Boolean {
        val next = queue.poll()

        if (next == null) {
            player.startTrack(null, false)
            return true
        }

        return player.startTrack(queue.poll(), false)
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        LOGGER.debug("TrackEndEvent intercepted! Track $track has ended from player $player with reason $endReason")

        if (endReason.mayStartNext) {
            LOGGER.debug("End reason was mayStartNext, attempting to start next track ${queue.peek()}")

            if (isLooping) {
                LOGGER.debug("Loop is enabled. Attempting to start clone of previous track $track with player $player")

                player.startTrack(queue.poll().makeClone(), false)
                return
            }

            nextTrack()
        }
    }

    override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
        LOGGER.debug("TrackStartEvent intercepted! Track $track has been started with player $player")
    }

    override fun onPlayerPause(player: AudioPlayer) {
        LOGGER.debug("PlayerPauseEvent intercepted! Pausing playing track ${player.playingTrack} for player $player")
    }

    override fun onPlayerResume(player: AudioPlayer) {
        LOGGER.debug("PlayerResumeEvent intercepted! Resuming paused track ${player.playingTrack} for player $player")
    }

    companion object {
        private val LOGGER = getLogger<TrackScheduler>()
    }
}