package me.bardy.bot

import org.springframework.boot.runApplication

fun main() {
    runApplication<BardyBotApplication> {
        setBanner(BardyBotBanner)
    }
}
