package dev.bombardy.bardybot.audio

enum class Result(val message: String) {

    SUCCESSFUL(""),
    CANNOT_JOIN_CHANNEL("**I can't play music if there isn't a channel to play music on!**"),
    USER_NOT_IN_CHANNEL("**I can't join you if you're not in a channel!**"),
    USER_NOT_IN_CHANNEL_WITH_BOT("**You need to be in the same channel as me to use my music commands!**")
}