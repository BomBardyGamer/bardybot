package me.bardy.bot.commands.music

import me.bardy.bot.command.BasicCommand
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.util.GuildMusicManagers
import org.springframework.stereotype.Component

@Component
class ClearQueueCommand(private val musicManagers: GuildMusicManagers) : BasicCommand("clear", setOf("cl", "cq")) {

    override fun execute(context: BotCommandContext) {
        val manager = musicManagers.getByGuild(context.guild)
        if (!manager.hasQueuedTracks()) {
            context.reply("I tried to clear the queue, but there wasn't any tracks to clear :pensive:")
            return
        }

        manager.clearQueue()
        context.reply("Bang! And the tracks are... gone...")
    }
}
