package me.bardy.bot.metrics

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import net.dv8tion.jda.api.sharding.ShardManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Suppress("unused")
@Component
@ConditionalOnProperty("services.metrics")
class Metrics(meterRegistry: MeterRegistry, shardManager: ShardManager) {

    val guildCount: Gauge = Gauge
        .builder("bardybot_guild_count") { shardManager.guildCache.size() }
        .description("The amount of guilds that BardyBot is currently in")
        .baseUnit("guilds")
        .register(meterRegistry)

    val voiceConnections: Gauge = Gauge
        .builder("bardybot_voice_connections") { shardManager.guildCache.filter { it.selfMember.voiceState != null }.size }
        .description("The amount of voice channels BardyBot is currently connected to")
        .baseUnit("voice connections")
        .register(meterRegistry)
}
