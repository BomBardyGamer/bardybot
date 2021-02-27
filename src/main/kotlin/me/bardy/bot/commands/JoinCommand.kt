package me.bardy.bot.commands

import me.bardy.bot.audio.Result
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandOptions
import me.bardy.bot.services.ConnectionService
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component

@Component
class JoinCommand(private val connectionService: ConnectionService) : Command("join") {

    override val options = CommandOptions(optionalArgs = true)

    override suspend fun execute(message: Message, channel: TextChannel, arguments: List<String>) {
        val requester = message.member ?: return
        val guild = message.guild

        val guildId = guild.id
        val botVoiceChannel = guild.selfMember.voiceState?.channel
        val voiceChannel = requester.voiceState?.channel

        connectionService.evalJoinResult(guildId, botVoiceChannel, voiceChannel).require(Result.SUCCESSFUL) {
            channel.sendMessage(it.message).queue()
            return
        }

        connectionService.join(requireNotNull(voiceChannel)).require(Result.SUCCESSFUL) {
            channel.sendMessage(it.message).queue()
            return
        }

        channel.sendMessage("**I've successfully joined the voice channel!**").queue()
    }
}