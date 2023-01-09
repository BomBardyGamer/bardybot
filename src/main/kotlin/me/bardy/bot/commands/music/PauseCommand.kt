package me.bardy.bot.commands.music

import me.bardy.bot.command.BasicCommand
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.audio.GuildMusicManagers
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component

@Component
class PauseCommand(private val musicManagers: GuildMusicManagers) : BasicCommand("pause", setOf("stop", "shutup", "stfu")) {

    override fun execute(context: BotCommandContext) {
        val manager = musicManagers.getByGuild(context.guild)
        if (!manager.isPaused()) {
            LOGGER.debug("Attempting to pause currently playing track ${manager.playingTrack()}")
            manager.pause()
            context.reply("Ah come on! I was listening to that.")
        }
    }

    companion object {

        private val LOGGER = LogManager.getLogger()
    }
}
