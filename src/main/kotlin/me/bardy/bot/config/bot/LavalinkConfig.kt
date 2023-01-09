package me.bardy.bot.config.bot

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("lavalink")
@JvmRecord
data class LavalinkConfig(val nodes: List<NodeConfig>) {

    @JvmRecord
    data class NodeConfig(val name: String, val url: String, val password: String)
}
