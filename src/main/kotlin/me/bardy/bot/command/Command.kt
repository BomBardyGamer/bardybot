package me.bardy.bot.command

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

abstract class Command(open val name: String) {

    open val options = CommandOptions()

    open val messages = CommandMessages()

    abstract suspend fun execute(message: Message, channel: TextChannel, arguments: List<String>)
}