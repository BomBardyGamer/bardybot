package me.bardy.bot.commands

import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandOptions
import me.bardy.bot.logger
import me.bardy.bot.services.TrackService
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component

@Component
class SkipCommand(private val trackService: TrackService) : Command("skip") {

    override val options = CommandOptions(listOf("sk"), true)

    override suspend fun execute(message: Message, channel: TextChannel, arguments: List<String>) {
        if (trackService.getMusicManager(message.guild.id).player.playingTrack == null) {
            channel.sendMessage("**I can't skip to the next track if there isn't one already playing to skip! You silly goose!**").queue()
            return
        }

        LOGGER.debug("Delegating track skipping to TrackService.")
        if (!trackService.skipTrack(message.textChannel.guild.id)) {
            channel.sendMessage("**I tried to skip to the next track but there wasn't a next track, so I've stopped the current track!**").queue()
            return
        }

        channel.sendMessage("**I've skipped the track!** *About time...*").queue()
    }

    companion object {

        private val LOGGER = logger<SkipCommand>()
    }
}