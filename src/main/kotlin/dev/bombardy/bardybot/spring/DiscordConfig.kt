package dev.bombardy.bardybot.spring

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "bot")
class DiscordConfig {

    lateinit var token: String
    lateinit var prefix: String
}