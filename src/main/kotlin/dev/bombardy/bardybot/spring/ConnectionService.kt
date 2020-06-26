package dev.bombardy.bardybot.spring

import dev.bombardy.bardybot.audio.MusicManager
import dev.bombardy.bardybot.getBean
import net.dv8tion.jda.api.entities.VoiceChannel
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ConnectionService @Autowired constructor(
        private val guildWrapperFactory: GuildWrapperFactory,
        private val beanFactory: BeanFactory
) {

    var inVoiceChannel = false

    fun join(channel: VoiceChannel) {
        guildWrapperFactory.create(channel.guild).guild.audioManager.openAudioConnection(channel)
        inVoiceChannel = true
    }

    fun leave(clearQueue: Boolean) {
        val musicManager = beanFactory.getBean<MusicManager>()
        if (clearQueue) {
            musicManager.scheduler.clearQueue()
            musicManager.player.stopTrack()
        }
        inVoiceChannel = false
    }
}