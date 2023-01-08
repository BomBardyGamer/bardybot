package me.bardy.bot.commands.misc

import me.bardy.bot.Versions
import me.bardy.bot.command.BasicCommand
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.util.Colors
import me.bardy.bot.util.color
import me.bardy.bot.util.description
import me.bardy.bot.util.embed
import me.bardy.bot.util.title
import net.dv8tion.jda.api.entities.MessageEmbed
import org.springframework.stereotype.Component

@Component
class VersionCommand : BasicCommand("version", setOf("v", "info")) {

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

    override fun execute(context: BotCommandContext) {
        context.reply(infoEmbed)
    }
}
