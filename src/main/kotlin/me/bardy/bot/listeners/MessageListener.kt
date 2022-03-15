package me.bardy.bot.listeners

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.exceptions.CommandSyntaxException
import me.bardy.bot.command.CommandContext
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

@Component
class MessageListener(private val dispatcher: CommandDispatcher<CommandContext>) : BardyBotListener() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.message.contentRaw.startsWith('!')) return
        val message = event.message.contentRaw.removePrefix("!")

        try {
            dispatcher.execute(parseCommand(CommandContext(event.guild, event.channel, event.member, event.message), message))
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
}
