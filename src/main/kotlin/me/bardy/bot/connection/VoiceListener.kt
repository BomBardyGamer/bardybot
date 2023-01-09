package me.bardy.bot.connection

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import me.bardy.bot.audio.GuildMusicManagers
import me.bardy.bot.util.BardyBotListener
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import org.springframework.stereotype.Component

@Component
class VoiceListener(
    private val connectionManager: ConnectionManager,
    private val musicManagers: GuildMusicManagers
) : BardyBotListener() {

    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val scheduledTasks = HashMap<String, ScheduledFuture<*>>()

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        val guild = event.guild
        val bot = guild.selfMember
        val botVoiceChannel = bot.voiceState?.channel ?: return

        // checks to ensure the bot doesn't leave when it's not supposed to
        if (event.channelLeft != botVoiceChannel) return // Not the bot's channel
        if (event.channelLeft.members.size > 1) return // More than one user (someone else) still in channel
        if (event.channelLeft.members[0] != bot) return // Remaining member not the bot
        musicManagers.getByGuild(guild).player.isPaused = true

        if (scheduledTasks.get(guild.id) != null) return
        val task = Runnable {
            connectionManager.leave(guild)
            musicManagers.removeForGuild(guild)
            complete(guild)
        }
        scheduledTasks.put(guild.id, scheduler.schedule(task, 5, TimeUnit.MINUTES))
    }

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        val guild = event.guild
        val botVoiceChannel = guild.selfMember.voiceState?.channel ?: return

        if (scheduledTasks.get(guild.id) == null) return // Won't cancel if there isn't one running
        if (event.entity == guild.selfMember) return // Won't run if the bot's the one joining
        if (event.channelJoined != botVoiceChannel) return

        complete(guild)
    }

    private fun complete(guild: Guild) {
        scheduledTasks.get(guild.id)?.cancel(false)
        scheduledTasks.remove(guild.id)
    }
}
