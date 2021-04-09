package me.bardy.bot.components

import lavalink.client.io.jda.JdaLavalink
import me.bardy.bot.config.BotConfig
import me.bardy.bot.config.LavalinkConfig
import me.bardy.bot.logger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
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
    fun lavalink(linkConfig: LavalinkConfig, botConfig: BotConfig) =
        JdaLavalink(botConfig.clientId, 1, null).apply {
            linkConfig.nodes.values.forEach { addNode(it.name, URI(it.url), it.password) }
        }

//    @Bean
//    fun jda(lavalink: JdaLavalink, config: BotConfig): ShardManager = runCatching {
//        val jda = DefaultShardManagerBuilder.create(config.token, GATEWAY_INTENTS)
//            .setActivity(Activity.playing("bot.bardy.me | !help"))
//            .setVoiceDispatchInterceptor(lavalink.voiceInterceptor)
//            .setMemberCachePolicy(MemberCachePolicy.VOICE)
//            .setChunkingFilter(ChunkingFilter.NONE)
//            .disableCache(DISABLED_FLAGS)
//            .build()
//
//        lavalink.setJdaProvider { jda.getShardById(it) }
//        return jda
//    }.getOrElse {
//        LOGGER.error("Your bot token is empty or invalid! You can configure your token by providing the setting the \"token\" value in application.yml to your token!")
//        exitProcess(0)
//    }

    @Bean
    fun shardManager(lavalink: JdaLavalink, config: BotConfig): ShardManager = try {
        val jda = DefaultShardManagerBuilder.create(config.token, GATEWAY_INTENTS)
            .setActivity(Activity.playing("bot.bardy.me | !help"))
            .setVoiceDispatchInterceptor(lavalink.voiceInterceptor)
            .setMemberCachePolicy(MemberCachePolicy.VOICE)
            .setChunkingFilter(ChunkingFilter.NONE)
            .disableCache(DISABLED_FLAGS)
            .build()

        lavalink.setJdaProvider { jda.getShardById(it) }
        jda
    } catch (exception: Exception) {
        LOGGER.error("Your bot token is empty or invalid! You can configure your token by setting the \"token\" value in application.yml")
        exitProcess(0)
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
            CacheFlag.MEMBER_OVERRIDES
        )

        private val LOGGER = logger<JDAComponent>()
    }
}