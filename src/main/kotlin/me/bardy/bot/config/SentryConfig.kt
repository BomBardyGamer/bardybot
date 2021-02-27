package me.bardy.bot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("sentry")
@ConstructorBinding
data class SentryConfig(val dsn: String?)