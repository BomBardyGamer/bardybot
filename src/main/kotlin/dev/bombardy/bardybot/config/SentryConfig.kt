package dev.bombardy.bardybot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "sentry")
class SentryConfig {

    lateinit var dsn: String
}