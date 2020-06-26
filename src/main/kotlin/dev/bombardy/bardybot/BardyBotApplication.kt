package dev.bombardy.bardybot

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.annotation.PostConstruct

@SpringBootApplication
class BardyBotApplication @Autowired constructor(private val beanFactory: BeanFactory) {

    @PostConstruct
    fun init() {
        val playerManager = beanFactory.getBean<AudioPlayerManager>()
        AudioSourceManagers.registerLocalSource(playerManager)
        AudioSourceManagers.registerRemoteSources(playerManager)
    }
}

fun main() {
    runApplication<BardyBotApplication>()
}

inline fun <reified T> BeanFactory.getBean() = getBean(T::class.java)