package dev.bombardy.bardybot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
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

    private var isLooping = false

    private val queue = ArrayBlockingQueue<AudioTrack>(100)

    fun queue(track: AudioTrack) {
        if (!player.startTrack(track, true)) queue.offer(track)
    }

    fun clearQueue() = queue.clear()

    fun nextTrack() = player.startTrack(queue.poll(), false)

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) player.startTrack(queue.poll(), true)
    }
}