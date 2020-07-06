package dev.bombardy.bardybot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager

/**
 * Represents a Guild Music Manager, used to get audio from the [TrackScheduler],
 * as well as load the [SendHandler] for the audio being sent to Discord.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
class MusicManager(manager: AudioPlayerManager) {

    val player: AudioPlayer = manager.createPlayer()
    val scheduler = TrackScheduler(player)

    init {
        player.addListener(scheduler)
    }

    val sendHandler = SendHandler(player)

    var isPaused = false
        set(paused) {
            player.isPaused = paused
            field = paused
        }

    var volume = 100
        set(volume) {
            player.volume = volume
            field = volume
        }
}