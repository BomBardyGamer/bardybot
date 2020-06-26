package dev.bombardy.bardybot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

/**
 * Represents an implementation of JDA's [AudioSendHandler], used for sending audio to
 * discord in 20 millisecond segments (what is required by Discord), as well as toggling
 * the OPUS handler to encode audio to OPUS format (also required by Discord)
 *
 * @author Callum Seabrook
 * @since 1.0
 */
class SendHandler(private val player: AudioPlayer) : AudioSendHandler {

    private val buffer = ByteBuffer.allocate(1024)
    private val frame = MutableAudioFrame().apply { setBuffer(buffer) }

    override fun provide20MsAudio() = buffer.flip() as ByteBuffer

    override fun canProvide() = player.provide(frame)

    override fun isOpus() = true
}