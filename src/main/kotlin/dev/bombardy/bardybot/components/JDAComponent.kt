package dev.bombardy.bardybot.components

import dev.bombardy.bardybot.JDAFunction
import dev.bombardy.bardybot.config.BotConfig
import dev.bombardy.bardybot.config.LavalinkConfig
import dev.bombardy.bardybot.getLogger
import dev.bombardy.octo.command.CommandManager
import lavalink.client.io.jda.JdaLavalink
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.net.URI
import kotlin.system.exitProcess

/**
 * Handles set up of [JDA] and [CommandManager], used for connecting
 * to the Discord API (and ultimately, the Discord bot), and handling
 * commands.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Component
class JDAComponent {

    @Bean
    fun jdaFunction() = JDAFunction()

    @Bean
    fun lavalink(jdaFunction: JDAFunction, linkConfig: LavalinkConfig, botConfig: BotConfig) = JdaLavalink(
            botConfig.clientId,
            1,
            jdaFunction
    ).apply { addNode(linkConfig.name, URI.create(linkConfig.url), linkConfig.password) }

    @Bean
    fun jda(jdaFunction: JDAFunction, lavalink: JdaLavalink, config: BotConfig): JDA = runCatching {
        val jda = JDABuilder.create(config.token, GATEWAY_INTENTS)
                .setActivity(Activity.playing("prevarinite.com"))
                .setVoiceDispatchInterceptor(lavalink.voiceInterceptor)
                .disableCache(DISABLED_FLAGS)
                .build()

        jdaFunction.jda = jda
        return jda
    }.getOrElse {
        LOGGER.error("Your bot token is empty or invalid! You can configure your token by providing the -Dbot.token=my_token argument when running this application")
        exitProcess(0)
    }

    @Bean
    fun commandManager(config: BotConfig, jda: JDA) = CommandManager(jda, config.prefix, COMMAND_MESSAGES)

    companion object {

        private val GATEWAY_INTENTS = listOf(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS
        )

        private val DISABLED_FLAGS = listOf(
                CacheFlag.ACTIVITY,
                CacheFlag.CLIENT_STATUS,
                CacheFlag.EMOTE,
                CacheFlag.MEMBER_OVERRIDES
        )

        private val COMMAND_MESSAGES = mapOf(
                "commandNotFound" to "Sorry, I couldn't find the command you were looking for."
        )

        private val LOGGER = getLogger<JDAComponent>()
    }
}