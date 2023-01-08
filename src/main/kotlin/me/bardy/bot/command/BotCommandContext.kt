package me.bardy.bot.command

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed

class BotCommandContext(
    val guild: Guild,
    val channel: MessageChannel,
    val member: Member?,
    val message: Message
) {

    fun getSelf(): Member = guild.selfMember

    fun reply(text: String) {
        channel.sendMessage(text).queue()
    }

    fun reply(message: Message) {
        channel.sendMessage(message).queue()
    }

    fun reply(embed: MessageEmbed) {
        channel.sendMessageEmbeds(embed).queue()
    }
}
