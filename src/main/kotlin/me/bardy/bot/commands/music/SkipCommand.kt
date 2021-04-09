package me.bardy.bot.commands.music

import me.bardy.bot.command.Command
import me.bardy.bot.components.ManagerMap
import me.bardy.bot.logger
import me.bardy.bot.services.TrackService
import org.springframework.stereotype.Component

@Component
class SkipCommand(
    private val musicManagers: ManagerMap,
    private val trackService: TrackService
) : Command("sk") {

    override fun register() = default("skip") {
        if (musicManagers[it.guild.id].player.playingTrack == null) {
            it.reply("I can't skip to the next track if there isn't one already playing to skip! You silly goose.")
            return@default
        }

        LOGGER.debug("Delegating track skipping to TrackService.")
        if (!trackService.skipTrack(it.guild.id)) {
            it.reply("I tried to skip to the next track, but there wasn't a next track, so I've stopped the current one.")
            return@default
        }

        it.reply("I've skipped the track! *About time...*")
    }

    companion object {

        private val LOGGER = logger<SkipCommand>()
    }
}