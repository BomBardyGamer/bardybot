package me.bardy.bot.commands.music

import me.bardy.bot.command.BasicCommand
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.services.TrackService
import me.bardy.bot.util.logger
import me.bardy.bot.util.GuildMusicManagers
import org.springframework.stereotype.Component

@Component
class SkipCommand(
    private val musicManagers: GuildMusicManagers,
    private val trackService: TrackService
) : BasicCommand("skip", setOf("sk")) {

    override fun execute(context: BotCommandContext) {
        if (musicManagers.getByGuild(context.guild).playingTrack() == null) {
            context.reply("I can't skip to the next track if there isn't one already playing to skip! You silly goose.")
            return
        }

        LOGGER.debug("Delegating track skipping to TrackService.")
        if (!trackService.skipTrack(context.guild)) {
            context.reply("I tried to skip to the next track, but there wasn't a next track, so I've stopped the current one.")
            return
        }

        context.reply("I've skipped the track! *About time...*")
    }

    companion object {

        private val LOGGER = logger<SkipCommand>()
    }
}
