package dev.bombardy.bardybot.audio

import net.dv8tion.jda.api.entities.MessageChannel

enum class Result(val message: String) {

    SUCCESSFUL(""),
    CHANNELS_NOT_EXIST("**I can't play music if there isn't a channel to play music on!**"),
    NO_PERMISSION_TO_JOIN("**You need to give me permission to join the channel you're in!**"),
    USER_NOT_IN_CHANNEL("**I can't join you if you're not in a channel!**"),
    USER_NOT_IN_CHANNEL_WITH_BOT("**You need to be in the same channel as me to use my music commands!**"),
    OTHER("**An unrecognised internal error has occurred. Please report to creator.**")
}

fun Result.handle(channel: MessageChannel) = when (message.isNotEmpty()) {
    true -> channel.sendMessage(message).queue()
    else -> Unit
}