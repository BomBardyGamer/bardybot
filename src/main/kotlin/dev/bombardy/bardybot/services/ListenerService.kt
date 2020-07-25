package dev.bombardy.bardybot.services

import dev.bombardy.bardybot.listeners.VoiceListener
import net.dv8tion.jda.api.JDA
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ListenerService(
        private val jda: JDA,
        private val connectionService: ConnectionService,
        private val trackService: TrackService
) {

    @PostConstruct
    fun registerListeners() {
        jda.addEventListener(VoiceListener(connectionService, trackService))
    }
}