package dev.bombardy.bardybot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager


class MusicManager(manager: AudioPlayerManager) {

    val player = manager.createPlayer()
    val scheduler = TrackScheduler(player)

    init {
        player.addListener(scheduler)
    }

    val sendHandler = SendHandler(player)
}