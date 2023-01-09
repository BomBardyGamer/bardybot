package me.bardy.bot.commands.misc

import me.bardy.bot.command.BasicCommand
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.services.ConnectionService
import net.dv8tion.jda.api.entities.VoiceChannel
import org.springframework.stereotype.Component

@Component
class JoinCommand(private val connectionService: ConnectionService) : BasicCommand("join", emptySet()) {

    override fun execute(context: BotCommandContext) {
        val member = context.member ?: return
        val voiceChannel = member.voiceState?.channel
        if (voiceChannel !is VoiceChannel) {
            context.reply("I can't join you if you're not in a channel!")
            return
        }

        val joinResult = connectionService.tryJoin(voiceChannel)
        context.reply(joinResult.message)
    }
}
