package dev.bombardy.bardybot.components

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class AudioPlayerManagerBean {

    @Bean
    fun audioPlayerManager(): AudioPlayerManager = DefaultAudioPlayerManager()
}