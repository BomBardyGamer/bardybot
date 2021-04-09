package me.bardy.bot.commands.misc

import me.bardy.bot.BARDY_ORANGE
import me.bardy.bot.Versions
import me.bardy.bot.command.Command
import me.bardy.bot.dsl.color
import me.bardy.bot.dsl.description
import me.bardy.bot.dsl.embed
import me.bardy.bot.dsl.title
import org.springframework.stereotype.Component

@Component
class VersionCommand : Command("v", "info") {

    val infoEmbed = embed {
        title("Here's some information about me:")
        description("""
            My version: [${Versions.BOT}](https://github.com/BomBardyGamer/BardyBot)
            
            __Powered By:__
            JDA - [${Versions.JDA}](https://github.com/DV8FromTheWorld/JDA)
            Lavalink Client - [${Versions.LAVALINK}](https://github.com/Frederikam/Lavalink-Client)
            Lavaplayer - [${Versions.LAVAPLAYER}](https://github.com/sedmelluq/lavaplayer)
        """.trimIndent())
        color(BARDY_ORANGE)
    }

    override fun register() = default("version") {
        it.reply(infoEmbed)
    }
}