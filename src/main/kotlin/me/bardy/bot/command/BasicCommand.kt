package me.bardy.bot.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder

abstract class BasicCommand(private val name: String, aliases: Set<String>) : Command(aliases) {

    abstract fun execute(context: BotCommandContext)

    final override fun create(): LiteralArgumentBuilder<BotCommandContext> = literal(name) {
        argument("arguments", StringArgumentType.greedyString()) {
            runs { execute(it.source) }
        }
        runs { execute(it.source) }
    }
}