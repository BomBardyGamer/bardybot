package me.bardy.bot

import java.util.*

object Versions {

    val BOT: String
    val JDA: String
    val LAVALINK: String
    val LAVAPLAYER: String

    init {
        val versionsFile = Properties().apply {
            load(Thread.currentThread().contextClassLoader.getResourceAsStream("META-INF/versions.properties"))
        }
        BOT = versionsFile.getProperty("bot")
        JDA = versionsFile.getProperty("jda")
        LAVALINK = versionsFile.getProperty("lavalink")
        LAVAPLAYER = versionsFile.getProperty("lavaplayer")
    }
}