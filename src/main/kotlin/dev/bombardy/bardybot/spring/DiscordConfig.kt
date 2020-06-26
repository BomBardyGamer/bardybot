package dev.bombardy.bardybot.spring

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Wrapper class for configuration values stored in application.properties
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "bot")
class DiscordConfig {

    /**
     * The token of the Discord Bot, used to connect to the Bot application
     * using Discord's API.
     */
    lateinit var token: String

    /**
     * The prefix of the bot. Will be configurable per-guild via in-guild
     * commands in a later version.
     */
    lateinit var prefix: String
}