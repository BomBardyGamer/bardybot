package me.bardy.bot.command

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.bardy.bot.config.BotConfig
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.sharding.ShardManager
import org.springframework.stereotype.Component

@Component
@Suppress("LeakingThis")
class CommandManager(shardManager: ShardManager, botConfig: BotConfig) : ListenerAdapter() {

    private val prefix = botConfig.prefix

    init {
        shardManager.addEventListener(this)
    }

    private val messageHandler = MessageHandler()
    private val commands = mutableListOf<Command>()

    fun register(command: Command) {
        commands += command
    }

    fun registerAll(commands: Collection<Command>) = commands.forEach(this::register)

    private suspend fun handle(channel: TextChannel, message: Message) {
        val iterator = message.contentDisplay.split(" ").iterator()
        val first = iterator.next()
        val commandName = if (first.startsWith(prefix)) first.removePrefix(prefix) else return

        var foundCommand = false
        commands.forEach {
            if (commandName != it.name && commandName !in it.options.aliases) return@forEach
            foundCommand = true

            val arguments = iterator.asSequence().toList()
            if (!it.options.optionalArgs && arguments.isEmpty()) {
                channel.sendMessage(it.messages.help).queue()
                return@forEach
            }

            if (message.author.isBot && !it.options.allowBots) {
                channel.sendMessage(it.messages.noBots).queue()
                return@forEach
            }

            if (it.options.isSynchronous) {
                runBlocking(Dispatchers.Main) { it.execute(message, channel, arguments) }
                return@forEach
            }

            it.execute(message, channel, arguments)
        }

        if (!foundCommand) messageHandler.sendMessage("commandNotFound", message)
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        GlobalScope.launch { handle(event.channel, event.message) }
    }
}