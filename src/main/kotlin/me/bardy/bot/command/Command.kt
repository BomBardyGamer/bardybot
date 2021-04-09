package me.bardy.bot.command

import com.mojang.brigadier.arguments.StringArgumentType.greedyString
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.tree.LiteralCommandNode

abstract class Command(vararg val aliases: String = emptyArray()) {

    abstract fun register(): LiteralCommandNode<CommandContext>

    fun default(name: String, context: (CommandContext) -> Unit): LiteralCommandNode<CommandContext> =
        literal<CommandContext>(name)
            .then(argument<CommandContext, String>("arguments", greedyString())
                .executes { context(it.source); 1 })
            .executes { context(it.source); 1 }
            .build()

    fun noArgs(name: String, context: (CommandContext) -> Unit): LiteralCommandNode<CommandContext> =
        literal<CommandContext>(name)
            .executes { context(it.source); 1 }
            .build()
}