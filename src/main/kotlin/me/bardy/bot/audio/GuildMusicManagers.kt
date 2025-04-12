package me.bardy.bot.audio

import dev.arbjerg.lavalink.client.LavalinkClient
import net.dv8tion.jda.api.entities.Guild

class GuildMusicManagers(private val lavalink: LavalinkClient) {

    private val managers = HashMap<String, MusicManager>()

    fun getByGuild(guild: Guild): MusicManager {
        val value = managers.get(guild.id)
        if (value != null) return value
        val manager = MusicManager(lavalink.getOrCreateLink(guild.idLong))
        managers.put(guild.id, manager)
        return manager
    }

    fun removeForGuild(guild: Guild) {
        managers.remove(guild.id)
    }
}
