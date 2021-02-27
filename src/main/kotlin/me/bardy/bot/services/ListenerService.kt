package me.bardy.bot.services

import me.bardy.bot.listeners.VoiceListener
import net.dv8tion.jda.api.sharding.ShardManager
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ListenerService(
    private val shardManager: ShardManager,
    private val voiceListener: VoiceListener
) {

    @PostConstruct
    fun registerListeners() = shardManager.addEventListener(voiceListener)
}