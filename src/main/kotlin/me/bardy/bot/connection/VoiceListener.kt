package me.bardy.bot.connection

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import me.bardy.bot.audio.GuildMusicManagers
import me.bardy.bot.util.BardyBotListener
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component

@Component
class VoiceListener(
    private val connectionManager: ConnectionManager,
    private val musicManagers: GuildMusicManagers
) : BardyBotListener() {

    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val scheduledTasks = HashMap<String, ScheduledFuture<*>>()

    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        try {
            handleEvent(event)
        } catch (exception: Exception) {
            LOGGER.error("Error whilst trying to handle event $event!", exception)
        }
    }

    private fun handleEvent(event: GuildVoiceUpdateEvent) {
        when {
            event.channelJoined != null -> onJoin(event.guild, event.entity, event.channelJoined!!.asVoiceChannel())
            event.channelLeft != null -> onLeave(event.guild, event.channelLeft!!.asVoiceChannel())
            else -> error("Channel joined and channel left were both null! This should be impossible!")
        }
    }

    private fun onJoin(guild: Guild, member: Member, channel: VoiceChannel) {
        val botVoiceChannel = guild.selfMember.voiceState?.channel ?: return

        if (scheduledTasks.get(guild.id) == null) return // Won't cancel if there isn't one running
        if (member == guild.selfMember) return // Won't run if the bot's the one joining
        if (channel != botVoiceChannel) return

        complete(guild)
    }

    private fun onLeave(guild: Guild, channel: VoiceChannel) {
        val bot = guild.selfMember
        val botVoiceChannel = bot.voiceState?.channel ?: return

        // checks to ensure the bot doesn't leave when it's not supposed to
        if (channel != botVoiceChannel) return // Not the bot's channel
        if (channel.members.size > 1) return // More than one user (someone else) still in channel
        if (channel.members.get(0) != bot) return // Remaining member not the bot
        musicManagers.getByGuild(guild).pause()

        if (scheduledTasks.get(guild.id) != null) return
        val task = Runnable {
            connectionManager.leave(guild)
            musicManagers.removeForGuild(guild)
            complete(guild)
        }
        scheduledTasks.put(guild.id, scheduler.schedule(task, 5, TimeUnit.MINUTES))
    }

    private fun complete(guild: Guild) {
        scheduledTasks.get(guild.id)?.cancel(false)
        scheduledTasks.remove(guild.id)
    }

    companion object {

        private val LOGGER = LogManager.getLogger()
    }
}
