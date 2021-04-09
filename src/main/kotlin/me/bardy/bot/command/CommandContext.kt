package me.bardy.bot.command

import net.dv8tion.jda.api.entities.*

data class CommandContext(
    val guild: Guild,
    val channel: TextChannel,
    val member: Member?,
    val message: Message
) {

    val self = guild.selfMember

    fun reply(text: String) = channel.sendMessage(text).queue()

    fun reply(message: Message) = channel.sendMessage(message).queue()

    fun reply(embed: MessageEmbed) = channel.sendMessage(embed).queue()
}