package me.bardy.bot.command

import net.dv8tion.jda.api.entities.Message

class MessageHandler {

    private val messages = mutableMapOf<String, (Message) -> Unit>()

    init {
        register("commandNotFound") {
            it.channel.sendMessage("Sorry, I couldn't find the command you were looking for.").queue()
        }
    }

    fun register(id: String, message: (Message) -> Unit) {
        messages[id] = message
    }

    fun sendMessage(id: String, message: Message) {
        val resolver = messages[id] ?: throw IllegalArgumentException("The message ID $id does not exist!")
        resolver(message)
    }
}