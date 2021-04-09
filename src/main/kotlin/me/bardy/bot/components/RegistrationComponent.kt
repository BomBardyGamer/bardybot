package me.bardy.bot.components

import com.mojang.brigadier.CommandDispatcher
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import lavalink.client.io.jda.JdaLavalink
import me.bardy.bot.audio.MusicManager
import me.bardy.bot.command.CommandContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

// responsible for registering various beans we need
@Component
class RegistrationComponent {

    @Bean
    fun audioPlayerManager(): AudioPlayerManager = DefaultAudioPlayerManager()

    @Bean
    fun commandDispatcher() = CommandDispatcher<CommandContext>()

    @Bean
    fun musicManagers(lavalink: JdaLavalink) = ManagerMap(lavalink)
}

/**
 * This class is a hack that allows us to use delegated inheritance and
 * override get without running into recursive issues.
 */
class ManagerMap(
    private val lavalink: JdaLavalink,
    private val map: MutableMap<String, MusicManager> = mutableMapOf()
) : MutableMap<String, MusicManager> by map {

    override fun get(key: String): MusicManager {
        val value = map[key]
        return if (value == null) {
            val answer = MusicManager(lavalink.getLink(key))
            put(key, answer)
            answer
        } else {
            value
        }
    }
}