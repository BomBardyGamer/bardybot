package dev.bombardy.bardybot.spring

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import dev.bombardy.bardybot.audio.MusicManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
class MusicManagerBean {

    @Bean
    @Scope("prototype")
    @Synchronized
    fun musicManager(audioPlayerManager: AudioPlayerManager) = MusicManager(audioPlayerManager)
}