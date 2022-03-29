package me.bardy.bot.commands.music

import com.mojang.brigadier.tree.LiteralCommandNode
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import me.bardy.bot.util.logger
import me.bardy.bot.util.ManagerMap
import org.springframework.stereotype.Component

@Component
class PauseCommand(private val musicManagers: ManagerMap) : Command("stop", "shutup", "stfu") {

    override fun register(): LiteralCommandNode<CommandContext> = default("pause") {
        val audioPlayer = musicManagers.get(it.guild.id).player
        if (!audioPlayer.isPaused) {
            LOGGER.debug("Attempting to pause currently playing track ${audioPlayer.playingTrack}")
            audioPlayer.isPaused = true
            it.reply("God damn you, I was listening to that.")
        }
    }

    companion object {

        private val LOGGER = logger<PauseCommand>()
    }
}
