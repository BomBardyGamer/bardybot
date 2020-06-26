package dev.bombardy.bardybot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager


class MusicManager(manager: AudioPlayerManager) {

    val player: AudioPlayer = manager.createPlayer().apply { addListener(scheduler) }
    val scheduler = TrackScheduler(player)

    val sendHandler = SendHandler(player)
}