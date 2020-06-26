package dev.bombardy.bardybot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.concurrent.ArrayBlockingQueue

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