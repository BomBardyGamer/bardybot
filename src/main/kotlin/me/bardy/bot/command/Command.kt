package me.bardy.bot.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder

abstract class Command(val aliases: Set<String>) {

    abstract fun create(): LiteralArgumentBuilder<BotCommandContext>
}
