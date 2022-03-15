package me.bardy.bot.components

import lavalink.client.io.jda.JdaLavalink
import me.bardy.bot.audio.MusicManager

class ManagerMap(private val lavalink: JdaLavalink) {

    private val map = mutableMapOf<String, MusicManager>()

    fun get(key: String): MusicManager {
        val value = map[key]
        if (value != null) return value

        val manager = MusicManager(lavalink.getLink(key))
        map[key] = manager
        return manager
    }

    fun remove(key: String) {
        map.remove(key)
    }
}
