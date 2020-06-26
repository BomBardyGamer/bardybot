package dev.bombardy.bardybot

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.annotation.PostConstruct

/**
 * The main Spring Boot Application class
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@SpringBootApplication
class BardyBotApplication @Autowired constructor(private val beanFactory: BeanFactory) {

    /**
     * Registers the local and remote sources for the bot to load music
     * from.
     *
     * Remote loading is restricted due to security flaws with the HTTP
     * audio source manager, allowing for users to play tracks to grab
     * the server's IP.
     */
    @PostConstruct
    fun init() {
        val playerManager = beanFactory.getBean<AudioPlayerManager>()
        AudioSourceManagers.registerLocalSource(playerManager)

        playerManager.registerSourceManager(YoutubeAudioSourceManager(true))
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault())
        playerManager.registerSourceManager(VimeoAudioSourceManager())
        playerManager.registerSourceManager(TwitchStreamAudioSourceManager())
    }
}

fun main() {
    runApplication<BardyBotApplication>()
}

/**
 * Gets the specified type [T] from its bean method using the [BeanFactory]
 *
 * @param T the type of the bean.
 * @return the type requested as [T]
 */
inline fun <reified T> BeanFactory.getBean() = getBean(T::class.java)