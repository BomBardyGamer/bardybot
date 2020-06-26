package dev.bombardy.bardybot.spring

import dev.bombardy.bardybot.audio.MusicManager
import dev.bombardy.bardybot.getBean
import net.dv8tion.jda.api.entities.VoiceChannel
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Handles connection to a voice channel, used by [TrackService] to join the
 * bot to a voice channel in order to play music.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Service
class ConnectionService @Autowired constructor(
        private val guildWrapperFactory: GuildWrapperFactory,
        private val beanFactory: BeanFactory
) {

    var inVoiceChannel = false

    /**
     * Connects the bot to the given voice channel, or, if the bot is
     * already in a channel, does nothing.
     */
    fun join(channel: VoiceChannel) {
        guildWrapperFactory.create(channel.guild).guild.audioManager.openAudioConnection(channel)
        inVoiceChannel = true
    }

    /**
     * Disconnects the bot from the voice channel it's currently in, and will stop
     * the currently playing track and clear the queue if [clearQueue] is true, or,
     * if the bot is not in a channel, does nothing.
     *
     * @param clearQueue if the queue should be cleared when the bot leaves the channel
     */
    fun leave(clearQueue: Boolean) {
        val musicManager = beanFactory.getBean<MusicManager>()
        if (clearQueue) {
            musicManager.scheduler.clearQueue()
            musicManager.player.stopTrack()
        }
        inVoiceChannel = false
    }
}