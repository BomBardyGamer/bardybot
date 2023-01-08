package me.bardy.bot

import io.sentry.Sentry
import me.bardy.bot.config.bot.SentryConfig
import me.bardy.bot.util.logger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import javax.annotation.PostConstruct

/**
 * The main Spring Boot Application class
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@SpringBootApplication
@ConfigurationPropertiesScan
class BardyBotApplication(private val sentryConfig: SentryConfig) {

    @PostConstruct
    fun initSentry() {
        if (sentryConfig.dsn == null) {
            LOGGER.warn("Sentry DSN was not present, skipping...")
            return
        }
        Sentry.init(sentryConfig.dsn)
    }

    companion object {

        private val LOGGER = logger<BardyBotApplication>()
    }
}
