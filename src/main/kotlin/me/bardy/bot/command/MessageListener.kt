package me.bardy.bot.command

import com.github.benmanes.caffeine.cache.Caffeine
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.exceptions.CommandSyntaxException
import me.bardy.bot.config.bot.BotConfig
import me.bardy.bot.config.bot.PermissionsConfig
import me.bardy.bot.util.BardyBotListener
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class MessageListener(
    private val botConfig: BotConfig,
    private val permissionsConfig: PermissionsConfig,
    private val dispatcher: CommandDispatcher<BotCommandContext>
) : BardyBotListener() {

    private val parsedCache = Caffeine.newBuilder()
        .maximumSize(32)
        .expireAfterWrite(Duration.ofMinutes(5))
        .build<CacheKey, ParseResults<BotCommandContext>> { dispatcher.parse(it.command, it.context) }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val member = event.member ?: return // Only support running commands in a guild
        if (!checkPermissions(member)) return

        val message = event.message.contentRaw
        if (!message.startsWith(botConfig.prefix)) return

        val command = message.substring(botConfig.prefix.length)
        val context = BotCommandContext(event.guild, event.channel, member)

        try {
            val parseResults = parsedCache.get(CacheKey(context, command))
            dispatcher.execute(parseResults)
        } catch (exception: CommandSyntaxException) {
            val commandName = command.split(" ").first()
            val rawMessage = exception.rawMessage.string
            event.channel.sendMessage("Error executing command '$commandName': $rawMessage (you did it wrong)").queue()
        } catch (exception: Throwable) {
            LOGGER.error("Unexpected error trying to execute command '$command'!", exception)
        }
    }

    private fun checkPermissions(member: Member): Boolean {
        if (!permissionsConfig.enabled) return true
        return member.roles.any { permissionsConfig.roles.contains(it.id) }
    }

    @JvmRecord
    private data class CacheKey(val context: BotCommandContext, val command: String)

    companion object {

        private val LOGGER = LogManager.getLogger()
    }
}
