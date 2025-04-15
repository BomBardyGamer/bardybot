package me.bardy.bot.command

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel

class BotCommandContext(val guild: Guild, val channel: MessageChannel, val member: Member) {

    fun getSelf(): Member = guild.selfMember

    fun reply(text: String) {
        channel.sendMessage(text).queue()
    }

    fun reply(embed: MessageEmbed) {
        channel.sendMessageEmbeds(embed).queue()
    }
}
