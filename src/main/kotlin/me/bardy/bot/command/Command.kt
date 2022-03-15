package me.bardy.bot.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode

abstract class Command(vararg val aliases: String = emptyArray()) {

    abstract fun register(): LiteralCommandNode<CommandContext>

    fun default(name: String, context: (CommandContext) -> Unit): LiteralCommandNode<CommandContext> = literal<CommandContext>(name) {
        argument("arguments", StringArgumentType.greedyString()) {
            runs { context(it.source) }
        }
        runs { context(it.source) }
    }.build()

    fun noArgs(name: String, context: (CommandContext) -> Unit): LiteralCommandNode<CommandContext> = literal<CommandContext>(name) {
        runs { context(it.source) }
    }.build()
}
