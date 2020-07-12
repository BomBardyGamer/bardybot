package dev.bombardy.bardybot.listeners

import dev.bombardy.bardybot.services.ConnectionService
import dev.bombardy.bardybot.services.TrackService
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*
import kotlin.concurrent.schedule

class VoiceListener(
        private val connectionService: ConnectionService,
        private val trackService: TrackService
) : ListenerAdapter() {

    private val timers = mutableMapOf<String, TimerTask>()

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        val guild = event.guild
        val guildId = guild.id
        val botVoiceChannel = guild.selfMember.voiceState?.channel ?: return

        if (event.channelLeft != botVoiceChannel) return // Not the bot's channel
        if (event.channelLeft.members.size > 1) return // More than one user (someone else) still in channel
        if (event.channelLeft.members[0] != guild.selfMember) return // Remaining member not the bot

        trackService.getMusicManager(guildId).player.isPaused = true

        if (timers[guildId] != null) return // Ensure the timer is not replaced or repeated if active

        val timerTask = Timer().schedule(300000) {
            connectionService.leave(event.guild.id, true)
            timers.remove(guildId)
        }

        timers[guildId] = timerTask
    }

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        val guild = event.guild
        val botVoiceChannel = guild.selfMember.voiceState?.channel ?: return

        if (event.channelJoined != botVoiceChannel) return

        timers[guild.id]?.cancel()
    }
}