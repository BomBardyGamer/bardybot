package me.bardy.bot.commands.music

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import kotlin.math.max
import kotlin.math.min
import lavalink.client.player.LavalinkPlayer
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import me.bardy.bot.command.argument
import me.bardy.bot.command.literal
import me.bardy.bot.command.runs
import me.bardy.bot.util.argument
import me.bardy.bot.util.ManagerMap
import org.springframework.stereotype.Component

@Component
class VolumeCommand(private val musicManagers: ManagerMap) : Command("vol") {

    override fun register(): LiteralCommandNode<CommandContext> = literal<CommandContext>("volume") {
        argument("value", IntegerArgumentType.integer(0, 200)) {
            runs { updateVolume(musicManagers.get(it.source.guild.id).player, it.argument("value"), it.source) }
        }
        argument("string", StringArgumentType.word()) {
            runs {
                val volume = try {
                    parseVolume(it.argument("string"))
                } catch (exception: Exception) {
                    it.source.reply("Computers may seem like magic sometimes, but we can't convert letters to numbers.")
                    return@runs
                }
                updateVolume(musicManagers.get(it.source.guild.id).player, volume, it.source)
            }
        }
    }.build()

    private fun parseVolume(value: String): Int {
        if (PERCENTAGE_REGEX.matches(value)) return min(200, max(0, value.removeSuffix("%").toInt()))
        return min(200, max(0, value.toInt()))
    }

    private fun updateVolume(player: LavalinkPlayer, volume: Int, context: CommandContext) {
        val action = if (volume > player.volume) "turned up the speakers" else "turned down the speakers"
        player.filters.volume = volume.toFloat()
        context.reply("I've $action to $volume!")
    }

    companion object {

        private val PERCENTAGE_REGEX = "(\\d+)%".toRegex()
    }
}
