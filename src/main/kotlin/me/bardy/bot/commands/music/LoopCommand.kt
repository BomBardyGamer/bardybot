package me.bardy.bot.commands.music

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import me.bardy.bot.util.argument
import me.bardy.bot.audio.TrackScheduler
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import me.bardy.bot.command.argument
import me.bardy.bot.command.literal
import me.bardy.bot.command.runs
import me.bardy.bot.components.ManagerMap
import org.springframework.stereotype.Component

@Component
class LoopCommand(private val musicManagers: ManagerMap) : Command("loop") {

    override fun register(): LiteralCommandNode<CommandContext> = literal<CommandContext>("loop") {
        argument("state", BoolArgumentType.bool()) {
            runs { toggleLoop(musicManagers.get(it.source.guild.id).scheduler, it.argument("state"), it.source) }
        }
        runs {
            val scheduler = musicManagers.get(it.source.guild.id).scheduler
            toggleLoop(scheduler, !scheduler.isLooping, it.source)
        }
    }.build()

    private fun toggleLoop(scheduler: TrackScheduler, state: Boolean, context: CommandContext) {
        if (!state) {
            context.reply("**Okay that's enough, I'll end the loop.**")
        } else {
            context.reply("**I'll start looping for you now.**")
        }
        scheduler.isLooping = state
    }
}
