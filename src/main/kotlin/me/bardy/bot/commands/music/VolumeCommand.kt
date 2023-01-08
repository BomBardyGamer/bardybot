package me.bardy.bot.commands.music

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlin.math.max
import kotlin.math.min
import me.bardy.bot.audio.MusicManager
import me.bardy.bot.command.Command
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.command.argument
import me.bardy.bot.command.getArgument
import me.bardy.bot.command.literal
import me.bardy.bot.command.runs
import me.bardy.bot.util.GuildMusicManagers
import org.springframework.stereotype.Component

@Component
class VolumeCommand(private val musicManagers: GuildMusicManagers) : Command(setOf("vol")) {

    override fun create(): LiteralArgumentBuilder<BotCommandContext> = literal("volume") {
        runs { context ->
            val manager = musicManagers.getByGuild(context.source.guild)
            context.source.reply("Speakers are turned up to **${manager.volume()}** at the moment!")
        }
        argument("value", IntegerArgumentType.integer(0, 200)) {
            runs { updateVolume(musicManagers.getByGuild(it.source.guild), it.getArgument("value"), it.source) }
        }
        argument("string", StringArgumentType.word()) {
            runs {
                val volume = try {
                    parseVolume(it.getArgument("string"))
                } catch (_: NumberFormatException) {
                    it.source.reply("Computers may seem like magic sometimes, but we can't convert letters to numbers.")
                    return@runs
                }
                updateVolume(musicManagers.getByGuild(it.source.guild), volume, it.source)
            }
        }
    }

    private fun parseVolume(value: String): Int {
        if (PERCENTAGE_REGEX.matches(value)) return min(200, max(0, value.removeSuffix("%").toInt()))
        return min(200, max(0, value.toInt()))
    }

    private fun updateVolume(manager: MusicManager, volume: Int, context: BotCommandContext) {
        val action = if (volume > manager.volume()) "turned up the speakers" else "turned down the speakers"
        manager.setVolume(volume)
        context.reply("I've $action to $volume!")
    }

    companion object {

        private val PERCENTAGE_REGEX = "(\\d+)%".toRegex()
    }
}
