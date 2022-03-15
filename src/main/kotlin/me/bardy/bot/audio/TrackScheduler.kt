package me.bardy.bot.audio

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import lavalink.client.player.IPlayer
import lavalink.client.player.event.PlayerEventListenerAdapter
import me.bardy.bot.util.logger
import java.util.Queue
import java.util.concurrent.ArrayBlockingQueue

/**
 * Represents a Guild Track Scheduler, used for queueing, clearing, skipping,
 * and autoplaying the next track in the queue. Also, will be responsible for
 * looping tracks where necessary in the future, though that is not implemented
 * yet.
 */
class TrackScheduler(private val player: IPlayer) : PlayerEventListenerAdapter() {

    var isLooping: Boolean = false
    val queue: Queue<AudioTrack> = ArrayBlockingQueue(100)

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
        if (isLooping) {
            LOGGER.debug("Loop is enabled. Attempting to start clone of previous track $track with player $player")
            player.playTrack(track.makeClone())
            return
        }

        nextTrack()
    }

    override fun onTrackStart(player: IPlayer, track: AudioTrack) {
        LOGGER.debug("TrackStartEvent intercepted! Track $track has been started with player $player")
    }

    override fun onPlayerPause(player: IPlayer) {
        LOGGER.debug("PlayerPauseEvent intercepted! Pausing playing track ${player.playingTrack} for player $player")
    }

    override fun onPlayerResume(player: IPlayer) {
        LOGGER.debug("PlayerResumeEvent intercepted! Resuming paused track ${player.playingTrack} for player $player")
    }

    companion object {

        private val LOGGER = logger<TrackScheduler>()
    }
}
