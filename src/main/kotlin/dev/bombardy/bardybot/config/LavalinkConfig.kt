package dev.bombardy.bardybot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "lavalink")
class LavalinkConfig {

    lateinit var name: String

    lateinit var password: String
}