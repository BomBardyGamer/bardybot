package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.getLogger
import dev.bombardy.bardybot.services.TrackService
import dev.bombardy.octo.command.Command
import net.dv8tion.jda.api.entities.Message

class SkipCommand(private val trackService: TrackService) : Command(listOf("skip", "sk"), true) {

    override suspend fun execute(message: Message, arguments: List<String>) {
        val channel = message.channel

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
        private val LOGGER = getLogger<SkipCommand>()
    }
}