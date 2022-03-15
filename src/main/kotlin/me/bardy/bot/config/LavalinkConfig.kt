package me.bardy.bot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("lavalink")
@ConstructorBinding
data class LavalinkConfig(val nodes: Map<String, NodeConfig>) {

    data class NodeConfig(val name: String, val url: String, val password: String)
}
