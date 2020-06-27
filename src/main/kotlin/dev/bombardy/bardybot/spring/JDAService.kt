package dev.bombardy.bardybot.spring

import dev.bombardy.bardybot.LOGGER
import me.mattstudios.mfjda.base.CommandManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.dv8tion.jda.internal.JDAImpl
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess

/**
 * Handles set up of [JDA] and [CommandManager], used for connecting
 * to the Discord API (and ultimately, the Discord bot), and handling
 * commands.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Service
class JDAService {

    @Bean
    fun jda(config: DiscordConfig) = runCatching {
        println(config.token)
        JDABuilder.create(config.token, GATEWAY_INTENTS)
                    .setActivity(Activity.playing("prevarinite.com"))
                    .disableCache(DISABLED_FLAGS)
                    .build()
    }.getOrElse {
        LOGGER.error("Your bot token is empty or invalid! You can configure your token by providing the -Dbot.token=my_token argument when running this application")
        exitProcess(0)
    }

    @Bean
    fun commandManager(config: DiscordConfig, jda: JDA) = CommandManager(jda, config.prefix)

    companion object {

        private val GATEWAY_INTENTS = listOf(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES)

        private val DISABLED_FLAGS = listOf(
                CacheFlag.ACTIVITY,
                CacheFlag.CLIENT_STATUS,
                CacheFlag.EMOTE)
    }
}