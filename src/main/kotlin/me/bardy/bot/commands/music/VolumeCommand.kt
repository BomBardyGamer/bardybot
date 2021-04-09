package me.bardy.bot.commands.music

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.tree.LiteralCommandNode
import lavalink.client.player.LavalinkPlayer
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import me.bardy.bot.components.ManagerMap
import org.springframework.stereotype.Component
import kotlin.math.max
import kotlin.math.min

@Component
class VolumeCommand(private val musicManagers: ManagerMap) : Command("vol") {

    override fun register(): LiteralCommandNode<CommandContext> = literal<CommandContext>("volume")
        .then(argument<CommandContext, Int>("value", IntegerArgumentType.integer())
            .executes {
                val player = musicManagers[it.source.guild.id].player
                val volume = min(200, max(0, it.getArgument("value", Int::class.java)))
                updateVolume(player, volume, it.source)
                1
            })
        .then(argument<CommandContext, String>("string", word())
            .executes {
                val player = musicManagers[it.source.guild.id].player
                val volume = try {
                    parseVolume(it.getArgument("string", String::class.java))
                } catch (exception: Exception) {
                    it.source.reply("Computers may seem like magic sometimes, but we can't convert letters to numbers.")
                    return@executes 1
                }
                updateVolume(player, volume, it.source)
                1
            })
        .build()

    private fun parseVolume(value: String): Int {
        if (value matches PERCENTAGE_REGEX) return min(200, max(0, value.removeSuffix("%").toInt()))
        return min(200, max(0, value.toInt()))
    }

    private fun updateVolume(player: LavalinkPlayer, volume: Int, context: CommandContext) {
        val action = if (volume > player.volume) "turned up the speakers" else "turned down the speakers"
        player.volume = volume
        context.reply("I've $action to ${volume}!")
    }

    companion object {

        private val PERCENTAGE_REGEX = "(\\d+)%".toRegex()
    }
}