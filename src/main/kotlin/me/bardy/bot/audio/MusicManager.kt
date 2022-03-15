package me.bardy.bot.audio

import lavalink.client.io.Link
import lavalink.client.player.LavalinkPlayer

/**
 * The music manager for a guild.
 */
class MusicManager(link: Link) {

    val player: LavalinkPlayer = link.player
    val scheduler: TrackScheduler = TrackScheduler(player)

    init {
        player.addListener(scheduler)
    }
}
