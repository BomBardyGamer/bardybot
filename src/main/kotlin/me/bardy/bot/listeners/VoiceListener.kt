package me.bardy.bot.listeners

import me.bardy.bot.components.ManagerMap
import me.bardy.bot.services.ConnectionService
import me.bardy.bot.services.TrackService
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component
import java.util.*
import kotlin.concurrent.schedule

@Component
class VoiceListener(
    private val connectionService: ConnectionService,
    private val musicManagers: ManagerMap
) : ListenerAdapter() {

    private val tasks = mutableMapOf<String, TimerTask>()

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        val guildId = event.guild.id
        val bot = event.guild.selfMember
        val botVoiceChannel = bot.voiceState?.channel ?: return

        // checks to ensure the bot doesn't leave when it's not supposed to
        if (event.channelLeft != botVoiceChannel) return // Not the bot's channel
        if (event.channelLeft.members.size > 1) return // More than one user (someone else) still in channel
        if (event.channelLeft.members[0] != bot) return // Remaining member not the bot
        musicManagers[guildId].player.isPaused = true

        if (tasks[guildId] != null) return
        tasks[guildId] = Timer().schedule(300000) {
            connectionService.leave(guildId)
            musicManagers -= guildId
            complete(guildId)
        }
    }

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        val guild = event.guild
        val botVoiceChannel = guild.selfMember.voiceState?.channel ?: return

        if (tasks[guild.id] == null) return // Won't cancel if there isn't one running
        if (event.entity == guild.selfMember) return // Won't run if the bot's the one joining
        if (event.channelJoined != botVoiceChannel) return

        complete(guild.id)
    }

    private fun complete(guildId: String) {
        tasks[guildId]?.cancel()
        tasks.remove(guildId)
    }
}