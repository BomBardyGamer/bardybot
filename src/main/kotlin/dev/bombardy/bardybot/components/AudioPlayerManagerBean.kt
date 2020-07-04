package dev.bombardy.bardybot.components

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * Factory class for the [AudioPlayerManager] bean method, used for getting the
 * singleton object of the [AudioPlayerManager] for the bot, used for playing music.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Component
class AudioPlayerManagerBean {

    @Bean
    fun audioPlayerManager(): AudioPlayerManager = DefaultAudioPlayerManager()
}