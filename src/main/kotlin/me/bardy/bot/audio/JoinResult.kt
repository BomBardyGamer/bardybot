package me.bardy.bot.audio

enum class JoinResult(val message: String) {

    SUCCESSFUL("Here I am! Let's get this party started! WOOOOO!!"),
    NO_CHANNELS("I can't play music if there isn't a channel to play music on!"),
    NO_PERMISSION_TO_JOIN("You need to give me permission to join the channel you're in!"),
    USER_NOT_IN_CHANNEL("I can't join you if you're not in a channel!"),
    USER_NOT_IN_CHANNEL_WITH_BOT("You need to be in the same channel as me to use my music commands!"),
    OTHER("An unrecognised internal error has occurred. Please report to creator.");
}
