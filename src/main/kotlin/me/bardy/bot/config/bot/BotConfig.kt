package me.bardy.bot.config.bot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * Wrapper class for configuration values stored in application.yml
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@ConfigurationProperties("bot")
@ConstructorBinding
data class BotConfig(val clientId: String, val token: String, val prefix: String)
