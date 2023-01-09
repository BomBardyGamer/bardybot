package me.bardy.bot.commands.music

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.bardy.bot.audio.MusicManager
import me.bardy.bot.command.Command
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.command.argument
import me.bardy.bot.command.getArgument
import me.bardy.bot.command.literal
import me.bardy.bot.command.runs
import me.bardy.bot.audio.GuildMusicManagers
import org.springframework.stereotype.Component

@Component
class LoopCommand(private val musicManagers: GuildMusicManagers) : Command(setOf("repeat")) {

    override fun create(): LiteralArgumentBuilder<BotCommandContext> = literal("loop") {
        argument("state", BoolArgumentType.bool()) {
            runs { toggleLoop(it.source, musicManagers.getByGuild(it.source.guild), it.getArgument("state")) }
        }
        runs {
            val manager = musicManagers.getByGuild(it.source.guild)
            toggleLoop(it.source, manager, !manager.isLooping())
        }
    }

    private fun toggleLoop(context: BotCommandContext, manager: MusicManager, state: Boolean) {
        if (state) {
            manager.startLooping()
            context.reply("I'll start looping for you now.")
        } else {
            manager.stopLooping()
            context.reply("Okay that's enough, I'll end the loop.")
        }
    }
}
