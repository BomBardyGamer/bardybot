package dev.bombardy.bardybot.commands

import dev.bombardy.bardybot.BARDY_ORANGE
import dev.bombardy.octo.command.Command
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message

class VersionCommand : Command(listOf("version", "v", "info"), true) {

    override suspend fun execute(message: Message, arguments: List<String>) {
         message.channel.sendMessage(EmbedBuilder()
                .setTitle("Here's a list of some awesome software I'm using:")
                .setDescription("""
                    **My Version:** [1.7-BETA](https://github.com/BomBardyGamer/BardyBot)
                    
                    **Powered By:**
                    Octo - [1.0.2](https://github.com/BomBardyGamer/Octo)
                    JDA - [4.2.0_183](https://github.com/DV8FromTheWorld/JDA)
                    Lavalink-Client - [41e1025cd4](https://github.com/FredBoat/Lavalink-Client)
                    Lavaplayer - [1.3.50](https://github.com/sedmelluq/lavaplayer)

                    Lavalink - [3.3.1.1](https://github.com/Frederikam/Lavalink)
                """.trimIndent())
                 .setColor(BARDY_ORANGE)
                 .build()
         ).queue()
    }

    fun getVersion() {

    }
}