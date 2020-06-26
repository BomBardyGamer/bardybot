package dev.bombardy.bardybot.spring

import me.mattstudios.mfjda.base.CommandManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class JDAService {

    @Bean
    fun jda(config: DiscordConfig) = JDABuilder.create(config.token, GATEWAY_INTENTS)
                .setActivity(Activity.playing("prevarinite.com"))
                .disableCache(DISABLED_FLAGS)
                .build()

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