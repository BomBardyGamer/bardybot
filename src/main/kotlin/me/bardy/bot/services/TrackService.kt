package me.bardy.bot.services

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import lavalink.client.io.jda.JdaLavalink
import me.bardy.bot.audio.LoadResultHandler
import me.bardy.bot.audio.MusicManager
import me.bardy.bot.audio.Result
import me.bardy.bot.logger
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Service

/**
 * Handles loading, playing, pausing, skipping, queueing, and volume of tracks.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Service
class TrackService(
    private val connectionService: ConnectionService,
    private val lavalink: JdaLavalink,
    private val playerManager: AudioPlayerManager
) {

    val musicManagers = mutableMapOf<String, MusicManager>()

    @Synchronized
    fun getMusicManager(guildId: String) = musicManagers.getOrPut(guildId, { MusicManager(lavalink.getLink(guildId)) })

    @Synchronized
    fun removeMusicManager(guildId: String) = musicManagers.remove(guildId)

    fun loadTrack(channel: TextChannel, track: String, requester: Member): Result {
        val musicManager = getMusicManager(channel.guild.id)
        val trackURL = if (URL_REGEX.matches(track)) track else "ytsearch:$track"

        if (requester.guild.voiceChannels.isEmpty()) {
            LOGGER.debug("No voice channels found in guild.")
            return Result.NO_CHANNELS
        }

        val guildId = requester.guild.id
        val channelId = requester.guild.selfMember.voiceState?.channel
        val voiceChannel = requester.voiceState?.channel

        connectionService.evalJoinResult(guildId, channelId, voiceChannel).require(Result.SUCCESSFUL) { return it }
        connectionService.join(requireNotNull(voiceChannel)).require(Result.SUCCESSFUL) { return it }

        channel.sendMessage("**I'm having a look around to see if I can find ** `$track`").queue()
        playerManager.loadItemOrdered(musicManager, trackURL, LoadResultHandler(channel, requester, trackURL, this))

        return Result.SUCCESSFUL
    }

    fun playTrack(guildId: String, audioTrack: AudioTrack) {
        getMusicManager(guildId).scheduler.queue(audioTrack)
    }

    fun queueTracks(tracks: List<AudioTrack>, requester: Member) {
        val musicManager = getMusicManager(requester.guild.id)
        tracks.forEach {
            musicManager.scheduler.queue(it)
            it.userData = requester
        }
    }

    fun skipTrack(guildId: String) = getMusicManager(guildId).scheduler.nextTrack()

    companion object {

        private val URL_REGEX = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
        private val LOGGER = logger<TrackService>()
    }
}