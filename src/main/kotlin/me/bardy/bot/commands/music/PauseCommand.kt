package me.bardy.bot.commands.music

import me.bardy.bot.command.Command
import me.bardy.bot.components.ManagerMap
import me.bardy.bot.logger
import org.springframework.stereotype.Component

@Component
class PauseCommand(private val musicManagers: ManagerMap) : Command("stop", "shutup", "stfu") {

    override fun register() = default("pause") {
        val audioPlayer = musicManagers[it.guild.id].player
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