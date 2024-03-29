package me.bardy.bot.audio

import lavalink.client.io.jda.JdaLavalink
import net.dv8tion.jda.api.entities.Guild

class GuildMusicManagers(private val lavalink: JdaLavalink) {

    private val managers = HashMap<String, MusicManager>()

    fun getByGuild(guild: Guild): MusicManager {
        val value = managers.get(guild.id)
        if (value != null) return value
        val manager = MusicManager(lavalink.getLink(guild.id))
        managers.put(guild.id, manager)
        return manager
    }

    fun removeForGuild(guild: Guild) {
        managers.remove(guild.id)
    }
}
