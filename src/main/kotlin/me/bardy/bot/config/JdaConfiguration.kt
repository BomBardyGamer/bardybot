package me.bardy.bot.config

import dev.arbjerg.lavalink.client.LavalinkClient
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener
import me.bardy.bot.config.bot.BotConfig
import me.bardy.bot.util.BardyBotListener
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.apache.logging.log4j.LogManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.EnumSet
import kotlin.system.exitProcess

@Configuration
class JdaConfiguration {

    @Bean
    fun shardManager(lavalink: LavalinkClient, config: BotConfig, listeners: Set<BardyBotListener>): ShardManager {
        try {
            return DefaultShardManagerBuilder.create(config.token, GATEWAY_INTENTS)
                .setActivity(Activity.playing("bot.bardy.me | ${config.prefix}help"))
                .setVoiceDispatchInterceptor(JDAVoiceUpdateListener(lavalink))
                .setMemberCachePolicy(MemberCachePolicy.VOICE)
                .setChunkingFilter(ChunkingFilter.NONE)
                .disableCache(EnumSet.complementOf(ENABLED_FLAGS))
                .addEventListeners(listeners)
                .build()
        } catch (_: Exception) {
            LOGGER.error("Your bot token is empty or invalid! You can configure your token by setting the \"token\" value in application.yml")
            exitProcess(0)
        }
    }

    companion object {

        private val GATEWAY_INTENTS = EnumSet.of(
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.MESSAGE_CONTENT
        )
        private val ENABLED_FLAGS = EnumSet.of(CacheFlag.VOICE_STATE)

        private val LOGGER = LogManager.getLogger()
    }
}
