package dev.bombardy.bardybot

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import dev.bombardy.bardybot.config.SentryConfig
import io.sentry.Sentry
import net.dv8tion.jda.api.entities.Member
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import javax.annotation.PostConstruct
import kotlin.time.Duration

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
        private val LOGGER = getLogger<BardyBotApplication>()
    }
}

fun main() {
    runApplication<BardyBotApplication>()
}

/**
 * Gets the SLF4J logger instance for the specified class [T] using the
 * [LoggerFactory.getLogger] method.
 *
 * @param T the class to log from
 * @return an instance of [Logger] for the specified class [T]
 */
inline fun <reified T> getLogger(): Logger = LoggerFactory.getLogger(T::class.java)

/**
 * Formats the [Duration] in to a specific format.
 *
 * @return the [Duration] in a specific format, as a [String]
 */
fun Duration.format() = when (inHours.toInt() > 0) {
    true -> String.format("%d:%02d:%02d", inHours.toInt(),
                inMinutes.minus(inHours.toInt() * 60).toInt(),
                inSeconds.minus(inMinutes.toInt() * 60).toInt())

    else -> String.format("%d:%02d", inMinutes.toInt(),
                inSeconds.minus(inMinutes.toInt() * 60).toInt())
}

/**
 * Formats the [Member]'s name in to a specific format.
 *
 * @return the [Member]'s name in a specific format, as a [String]
 */
fun Member.formatName() = when (nickname) {
    null -> user.asTag
    else -> "${user.asTag} (also known as $nickname)"
}

/**
 * Modified [Iterable.sumBy] to allow for long values to be supplied and
 * returned instead of integer values.
 */
inline fun <T> Iterable<T>.sumBy(selector: (T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

const val BARDY_ORANGE = 16737792