package me.bardy.bot.commands.misc

import com.mojang.brigadier.tree.LiteralCommandNode
import me.bardy.bot.Versions
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import me.bardy.bot.util.Colors
import me.bardy.bot.util.color
import me.bardy.bot.util.description
import me.bardy.bot.util.embed
import me.bardy.bot.util.title
import net.dv8tion.jda.api.entities.MessageEmbed
import org.springframework.stereotype.Component

@Component
class VersionCommand : Command("v", "info") {

    val infoEmbed: MessageEmbed = embed {
        title("Here's some information about me:")
        description("""
            My version: [${Versions.BOT}](https://github.com/BomBardyGamer/BardyBot)
            
            __Powered By:__
            JDA - [${Versions.JDA}](https://github.com/DV8FromTheWorld/JDA)
            Lavalink Client - [${Versions.LAVALINK}](https://github.com/Frederikam/Lavalink-Client)
            Lavaplayer - [${Versions.LAVAPLAYER}](https://github.com/sedmelluq/lavaplayer)
        """.trimIndent())
        color(Colors.BARDY_ORANGE)
    }

    override fun register(): LiteralCommandNode<CommandContext> = default("version") {
        it.reply(infoEmbed)
    }
}
