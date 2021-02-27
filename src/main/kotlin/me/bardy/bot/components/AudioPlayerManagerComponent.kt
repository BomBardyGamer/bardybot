package me.bardy.bot.components

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class AudioPlayerManagerComponent {

    @Bean
    fun audioPlayerManager(): AudioPlayerManager = DefaultAudioPlayerManager()
}