package me.bardy.bot.commands.misc

import me.bardy.bot.audio.Result
import me.bardy.bot.command.Command
import me.bardy.bot.services.ConnectionService
import org.springframework.stereotype.Component

@Component
class JoinCommand(private val connectionService: ConnectionService) : Command() {

    override fun register() = default("join") { context ->
        val guildId = context.guild.id
        val botVoiceChannel = context.self.voiceState?.channel
        val voiceChannel = (context.member ?: return@default).voiceState?.channel

        connectionService.evalJoinResult(guildId, botVoiceChannel, voiceChannel).require(Result.SUCCESSFUL) {
            context.reply(it.message)
            return@default
        }

        connectionService.join(voiceChannel!!)
        context.reply("**I've successfully joined the voice channel!**")
    }
}