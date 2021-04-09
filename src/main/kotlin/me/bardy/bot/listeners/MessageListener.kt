package me.bardy.bot.listeners

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.exceptions.CommandSyntaxException
import me.bardy.bot.command.CommandContext
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.sharding.ShardManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class MessageListener(
    private val dispatcher: CommandDispatcher<CommandContext>,
    private val shardManager: ShardManager
) : ListenerAdapter() {

    @PostConstruct
    fun registerSelf() = shardManager.addEventListener(this)

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (!event.message.contentRaw.startsWith('!')) return
        val message = event.message.contentRaw.removePrefix("!")

        try {
            dispatcher.execute(parseCommand(event.buildContext(), message))
        } catch (exception: CommandSyntaxException) {
            val exceptionMessage = when (exception.type) {
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException() -> "a parse exception"
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument() -> "an unknown argument exception"
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand() -> "an unknown command exception"
                else -> "Unknown error when attempting to execute command $message"
            }
            event.channel.sendMessage("I seem to have encountered $exceptionMessage when trying to execute that.").queue()
        }
    }

    @Cacheable("parse_results")
    fun parseCommand(context: CommandContext, command: String): ParseResults<CommandContext> = dispatcher.parse(command, context)

    private fun GuildMessageReceivedEvent.buildContext() = CommandContext(guild, channel, member, message)
}