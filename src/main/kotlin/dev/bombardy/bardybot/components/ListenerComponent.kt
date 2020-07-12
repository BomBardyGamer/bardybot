package dev.bombardy.bardybot.components

import dev.bombardy.bardybot.listeners.VoiceListener
import dev.bombardy.bardybot.services.ConnectionService
import dev.bombardy.bardybot.services.TrackService
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class ListenerComponent {

    @Bean
    fun voiceListener(connectionService: ConnectionService, trackService: TrackService)
            = VoiceListener(connectionService, trackService)
}