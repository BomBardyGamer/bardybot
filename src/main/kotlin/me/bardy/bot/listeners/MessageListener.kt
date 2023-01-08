package me.bardy.bot.listeners

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.exceptions.CommandSyntaxException
import me.bardy.bot.command.BotCommandContext
import me.bardy.bot.config.bot.BotConfig
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.apache.logging.log4j.LogManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
class MessageListener(
    private val botConfig: BotConfig,
    private val dispatcher: CommandDispatcher<BotCommandContext>
) : BardyBotListener() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.message.contentRaw.startsWith(botConfig.prefix)) return
        val command = event.message.contentRaw.removePrefix(botConfig.prefix)
        val context = BotCommandContext(event.guild, event.channel, event.member, event.message)

        try {
            val parseResults = parseCommand(context, command)
            dispatcher.execute(parseResults)
        } catch (exception: CommandSyntaxException) {
            val commandName = command.split(" ").first()
            val exceptionMessage = exception.rawMessage.string
            val message = "I seem to have encountered an error when trying to execute command '$commandName': $exceptionMessage"
            event.channel.sendMessage(message).queue()
        } catch (exception: Throwable) {
            LOGGER.error("Unexpected error trying to execute command '$command'!", exception)
        }
    }

    @Cacheable("parse_results")
    fun parseCommand(context: BotCommandContext, command: String): ParseResults<BotCommandContext> = dispatcher.parse(command, context)

    companion object {

        private val LOGGER = LogManager.getLogger()
    }
}
