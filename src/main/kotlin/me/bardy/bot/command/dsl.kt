package me.bardy.bot.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext

inline fun <S> literal(
    name: String,
    builder: LiteralArgumentBuilder<S>.() -> Unit
): LiteralArgumentBuilder<S> = LiteralArgumentBuilder.literal<S>(name).apply(builder)

inline fun <S> LiteralArgumentBuilder<S>.literal(
    name: String,
    builder: LiteralArgumentBuilder<S>.() -> Unit
): LiteralArgumentBuilder<S> = then(LiteralArgumentBuilder.literal<S>(name).apply(builder))

inline fun <S, T> LiteralArgumentBuilder<S>.argument(
    name: String,
    type: ArgumentType<T>,
    builder: RequiredArgumentBuilder<S, T>.() -> Unit
): LiteralArgumentBuilder<S> = then(RequiredArgumentBuilder.argument<S, T>(name, type).apply(builder))

inline fun <S, T, T1> RequiredArgumentBuilder<S, T>.argument(
    name: String,
    type: ArgumentType<T1>,
    builder: RequiredArgumentBuilder<S, T1>.() -> Unit
): RequiredArgumentBuilder<S, T> = then(RequiredArgumentBuilder.argument<S, T1>(name, type).apply(builder))

inline fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.runs(crossinline action: (CommandContext<S>) -> Unit): ArgumentBuilder<S, T> {
    return executes {
        action(it)
        com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}

inline fun <reified T> CommandContext<BotCommandContext>.getArgument(name: String): T = getArgument(name, T::class.java)
