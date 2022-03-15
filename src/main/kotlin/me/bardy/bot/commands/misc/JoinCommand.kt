package me.bardy.bot.commands.misc

import com.mojang.brigadier.tree.LiteralCommandNode
import me.bardy.bot.audio.JoinResult
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import me.bardy.bot.services.ConnectionService
import net.dv8tion.jda.api.entities.VoiceChannel
import org.springframework.stereotype.Component

@Component
class JoinCommand(private val connectionService: ConnectionService) : Command() {

    override fun register(): LiteralCommandNode<CommandContext> = default("join") { context ->
        val member = context.member ?: return@default
        val botVoiceChannel = context.self.voiceState?.channel
        val voiceChannel = member.voiceState?.channel
        if (voiceChannel !is VoiceChannel?) return@default

        connectionService.evaluateJoin(botVoiceChannel, voiceChannel).require(JoinResult.SUCCESSFUL) {
            context.reply(it.message)
            return@default
        }

        connectionService.join(voiceChannel!!)
        context.reply("**I've successfully joined the voice channel!**")
    }
}
