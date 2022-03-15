package me.bardy.bot.services

import net.dv8tion.jda.api.sharding.ShardManager
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import me.bardy.bot.listeners.BardyBotListener

@Service
class ListenerService(
    private val shardManager: ShardManager,
    private val listeners: Set<BardyBotListener>
) {

    @PostConstruct
    fun registerListeners() {
        shardManager.addEventListener(*listeners.toTypedArray())
    }
}
