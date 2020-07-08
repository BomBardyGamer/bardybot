package dev.bombardy.bardybot.audio

import lavalink.client.io.Link
import lavalink.client.player.LavalinkPlayer

/**
 * Represents a Guild Music Manager, used to get audio from the [TrackScheduler].
 *
 * @author Callum Seabrook
 * @since 1.0
 */
class MusicManager(link: Link) {

    val player: LavalinkPlayer = link.player
    val scheduler = TrackScheduler(player)

    init {
        player.addListener(scheduler)
    }
}