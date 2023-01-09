package me.bardy.bot.commands.misc

import me.bardy.bot.command.BasicCommand
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.connection.ConnectionManager
import me.bardy.bot.audio.GuildMusicManagers
import org.springframework.stereotype.Component

@Component
class LeaveCommand(
    private val connectionManager: ConnectionManager,
    private val musicManagers: GuildMusicManagers
) : BasicCommand("leave", emptySet()) {

    override fun execute(context: BotCommandContext) {
        val requester = context.member ?: return
        val botVoiceChannel = context.getSelf().voiceState?.channel
        if (botVoiceChannel == null) {
            context.reply("**I can't leave a channel if I'm not in one!**")
            return
        }
        if (requester.voiceState?.channel != botVoiceChannel) {
            context.reply("**You have to be in the same channel as me to make me leave**")
            return
        }

        connectionManager.leave(context.guild)
        musicManagers.removeForGuild(context.guild)
        context.reply("**I've successfully left the voice channel!**")
    }
}
