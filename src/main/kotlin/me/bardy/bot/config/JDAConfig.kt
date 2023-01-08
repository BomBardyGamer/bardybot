package me.bardy.bot.config

import lavalink.client.io.jda.JdaLavalink
import me.bardy.bot.config.bot.BotConfig
import me.bardy.bot.listeners.BardyBotListener
import me.bardy.bot.util.logger
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.system.exitProcess

@Configuration
class JDAConfig {

    @Bean
    fun shardManager(lavalink: JdaLavalink, config: BotConfig, listeners: Set<BardyBotListener>): ShardManager {
        try {
            val jda = DefaultShardManagerBuilder.create(config.token, GATEWAY_INTENTS)
                .setActivity(Activity.playing("bot.bardy.me | ${config.prefix}help"))
                .setVoiceDispatchInterceptor(lavalink.voiceInterceptor)
                .setMemberCachePolicy(MemberCachePolicy.VOICE)
                .setChunkingFilter(ChunkingFilter.NONE)
                .disableCache(DISABLED_FLAGS)
                .build()
            lavalink.setJdaProvider(jda::getShardById)
            jda.addEventListener(*listeners.toTypedArray())
            return jda
        } catch (_: Exception) {
            LOGGER.error("Your bot token is empty or invalid! You can configure your token by setting the \"token\" value in application.yml")
            exitProcess(0)
        }
    }

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
            CacheFlag.MEMBER_OVERRIDES,
            CacheFlag.ONLINE_STATUS,
            CacheFlag.ROLE_TAGS
        )

        private val LOGGER = logger<JDAConfig>()
    }
}
