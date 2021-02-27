package me.bardy.bot.commands

import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandOptions
import me.bardy.bot.dsl.description
import me.bardy.bot.dsl.embed
import me.bardy.bot.dsl.title
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component

@Component
class VersionCommand : Command("version") {

    override val options = CommandOptions(listOf("version", "v", "info"), true)

    override suspend fun execute(message: Message, channel: TextChannel, arguments: List<String>) {
//         message.channel.sendMessage(EmbedBuilder()
//                .setTitle("Here's a list of some awesome software I'm using:")
//                .setDescription("""
//                    **My Version:** [1.7-BETA](https://github.com/BomBardyGamer/BardyBot)
//
//                    **Powered By:**
//                    Octo - [1.0.2](https://github.com/BomBardyGamer/Octo)
//                    JDA - [4.2.0_183](https://github.com/DV8FromTheWorld/JDA)
//                    Lavalink-Client - [41e1025cd4](https://github.com/FredBoat/Lavalink-Client)
//                    Lavaplayer - [1.3.50](https://github.com/sedmelluq/lavaplayer)
//
//                    Lavalink - [3.3.1.1](https://github.com/Frederikam/Lavalink)
//                """.trimIndent())
//                 .setColor(BARDY_ORANGE)
//                 .build()
//         ).queue()
        channel.sendMessage(embed {
            title = "Here's some information about me:"
            description = """
                **My Version:** [1.7.1-BETA](https://github.com/BomBardyGamer/bardybot)

                **Powered By:**
                JDA - [4.2.0_229](https://github.com/DV8FromTheWorld/JDA)
                Lavalink-Client - [44ce5a5](https://github.com/Frederikam/Lavalink-Client)
                Lavaplayer - [1.3.71](https://github.com/sedmelluq/lavaplayer)

                Created by [BomBardyGamer](https://github.com/BomBardyGamer)
            """.trimIndent()
        }).queue()
    }
}