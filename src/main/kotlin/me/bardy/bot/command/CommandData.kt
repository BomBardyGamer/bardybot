package me.bardy.bot.command

import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message

data class CommandOptions(
    val aliases: List<String> = emptyList(),
    val optionalArgs: Boolean = false,
    val allowBots: Boolean = false,
    val isSynchronous: Boolean = false
)

data class CommandMessages(
    val help: Message = "Use the command properly and you wouldn't see this".toMessage(),
    val noBots: Message = "Sorry, bots can't execute commands!".toMessage()
)

fun String.toMessage() = MessageBuilder(this).build()