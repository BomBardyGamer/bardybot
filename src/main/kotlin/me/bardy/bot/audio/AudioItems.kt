package me.bardy.bot.audio

import dev.arbjerg.lavalink.client.player.PlaylistLoaded
import dev.arbjerg.lavalink.client.player.Track
import dev.arbjerg.lavalink.protocol.v4.PlaylistInfo
import net.dv8tion.jda.api.entities.Member

sealed interface AudioItem {

    val requester: Member
}

class AudioPlaylist(val info: PlaylistInfo, val tracks: List<Track>, override val requester: Member) : AudioItem

class AudioTrack(val track: Track, override val requester: Member) : AudioItem
