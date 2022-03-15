package me.bardy.bot

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import io.sentry.Sentry
import me.bardy.bot.config.SentryConfig
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
class BardyBotApplication(
    private val audioPlayerManager: AudioPlayerManager,
    private val sentryConfig: SentryConfig
) {

    /**
     * Registers the local and remote sources for the bot to load music
     * from.
     *
     * Remote loading is restricted due to security flaws with the HTTP
     * audio source manager, allowing for users to play tracks to grab
     * the server's IP.
     */
    @PostConstruct
    fun initSourceManagers() {
        AudioSourceManagers.registerLocalSource(audioPlayerManager)

        audioPlayerManager.registerSourceManager(YoutubeAudioSourceManager(true))
        audioPlayerManager.registerSourceManager(SoundCloudAudioSourceManager.builder().withAllowSearch(true).build())
        audioPlayerManager.registerSourceManager(VimeoAudioSourceManager())
        audioPlayerManager.registerSourceManager(TwitchStreamAudioSourceManager("zlgewfd7yonsfhsxslto0fsiy0uvoc"))

        audioPlayerManager.setTrackStuckThreshold(5000)
    }

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
