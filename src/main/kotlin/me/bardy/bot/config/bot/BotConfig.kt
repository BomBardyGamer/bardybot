package me.bardy.bot.config.bot

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Wrapper class for configuration values stored in application.yml
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@ConfigurationProperties("bot")
@JvmRecord
data class BotConfig(val token: String, val prefix: String)
