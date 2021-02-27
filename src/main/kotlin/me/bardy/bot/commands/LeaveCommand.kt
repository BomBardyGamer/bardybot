package me.bardy.bot.commands

import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandOptions
import me.bardy.bot.services.ConnectionService
import me.bardy.bot.services.TrackService
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component

@Component
class LeaveCommand(
    private val connectionService: ConnectionService,
    private val trackService: TrackService
) : Command("leave") {

    override val options = CommandOptions(optionalArgs = true)

    override suspend fun execute(message: Message, channel: TextChannel, arguments: List<String>) {
        val requester = message.member ?: return

        val botVoiceChannel = message.guild.selfMember.voiceState?.channel
        if (botVoiceChannel == null) {
            channel.sendMessage("**I can't leave a channel if I'm not in one!**").queue()
            return
        }

        if (requester.voiceState?.channel != botVoiceChannel) {
            channel.sendMessage("**You have to be in the same channel as me to make me leave**").queue()
            return
        }

        val guildId = message.guild.id
        connectionService.leave(guildId)
        trackService.removeMusicManager(guildId)
        channel.sendMessage("**I've successfully left the voice channel!**").queue()
    }
}