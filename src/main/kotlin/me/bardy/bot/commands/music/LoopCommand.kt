package me.bardy.bot.commands.music

import com.mojang.brigadier.arguments.BoolArgumentType.bool
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.tree.LiteralCommandNode
import me.bardy.bot.audio.TrackScheduler
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import me.bardy.bot.components.ManagerMap
import org.springframework.stereotype.Component

@Component
class LoopCommand(private val musicManagers: ManagerMap) : Command("loop") {

    override fun register(): LiteralCommandNode<CommandContext> = literal<CommandContext>("loop")
        .then(argument<CommandContext, Boolean>("state", bool()).executes {
            toggleLoop(
                musicManagers[it.source.guild.id].scheduler,
                it.getArgument("state", Boolean::class.java),
                it.source
            )
            1
        })
        .executes {
            val scheduler = musicManagers[it.source.guild.id].scheduler
            toggleLoop(scheduler, !scheduler.isLooping, it.source)
            1
        }
        .build()

    private fun toggleLoop(scheduler: TrackScheduler, state: Boolean, context: CommandContext) {
        context.reply(if (!state) {
            "**Okay that's enough, I'll end the loop.**"
        } else {
            "**I'll start looping for you now.**"
        })

        scheduler.isLooping = state
    }
}