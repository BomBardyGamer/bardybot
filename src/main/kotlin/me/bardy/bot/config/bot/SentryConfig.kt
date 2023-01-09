package me.bardy.bot.config.bot

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("sentry")
@JvmRecord
data class SentryConfig(val dsn: String?)
