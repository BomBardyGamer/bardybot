package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.getLogger
import dev.bombardy.bardybot.services.TrackService
import dev.bombardy.octo.command.Command
import net.dv8tion.jda.api.entities.Message

class SkipCommand(private val trackService: TrackService) : Command(listOf("skip", "sk"), true) {

    override suspend fun execute(message: Message, arguments: List<String>) {
        LOGGER.debug("Delegating track skipping to TrackService.")
        if (!trackService.skipTrack(message.textChannel.guild.idLong)) {
            message.channel.sendMessage("**I cannot skip the track if there isn't a track playing!**").queue()
            return
        }

        message.channel.sendMessage("**I've skipped the track!** *About time...*").queue()
    }

    companion object {
        private val LOGGER = getLogger<SkipCommand>()
    }
}