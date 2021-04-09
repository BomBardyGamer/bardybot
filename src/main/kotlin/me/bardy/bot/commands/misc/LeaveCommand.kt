package me.bardy.bot.commands.misc

import me.bardy.bot.command.Command
import me.bardy.bot.components.ManagerMap
import me.bardy.bot.services.ConnectionService
import org.springframework.stereotype.Component

@Component
class LeaveCommand(
    private val connectionService: ConnectionService,
    private val musicManagers: ManagerMap
) : Command() {

    override fun register() = default("leave") {
        val requester = it.member ?: return@default

        val botVoiceChannel = it.self.voiceState?.channel
        if (botVoiceChannel == null) {
            it.reply("**I can't leave a channel if I'm not in one!**")
            return@default
        }

        if (requester.voiceState?.channel != botVoiceChannel) {
            it.reply("**You have to be in the same channel as me to make me leave**")
            return@default
        }

        val guildId = it.guild.id
        connectionService.leave(guildId)
        musicManagers -= guildId
        it.reply("**I've successfully left the voice channel!**")
    }
}