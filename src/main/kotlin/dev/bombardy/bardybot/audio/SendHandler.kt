package dev.bombardy.bardybot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

class SendHandler(private val player: AudioPlayer) : AudioSendHandler {

    private val buffer = ByteBuffer.allocate(1024)
    private val frame = MutableAudioFrame().apply { setBuffer(buffer) }

    override fun provide20MsAudio() = buffer.flip() as ByteBuffer

    override fun canProvide() = player.provide(frame)

    override fun isOpus() = true
}