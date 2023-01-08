package me.bardy.bot.commands.misc

import me.bardy.bot.audio.JoinResult
import me.bardy.bot.command.BasicCommand
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.services.ConnectionService
import net.dv8tion.jda.api.entities.VoiceChannel
import org.springframework.stereotype.Component

@Component
class JoinCommand(private val connectionService: ConnectionService) : BasicCommand("join", emptySet()) {

    override fun execute(context: BotCommandContext) {
        val member = context.member ?: return
        val botVoiceChannel = context.getSelf().voiceState?.channel
        val voiceChannel = member.voiceState?.channel
        if (voiceChannel !is VoiceChannel?) return

        val joinResult = connectionService.evaluateJoin(botVoiceChannel, voiceChannel)
        if (joinResult != JoinResult.SUCCESSFUL) {
            context.reply(joinResult.message)
            return
        }

        connectionService.join(voiceChannel!!)
        context.reply("**I've successfully joined the voice channel!**")
    }
}
