package dev.bombardy.bardybot.metrics

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import lavalink.client.io.jda.JdaLavalink
import net.dv8tion.jda.api.JDA
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Suppress("unused")
@Component
@ConditionalOnProperty("services.metrics")
class Metrics(
        meterRegistry: MeterRegistry,
        jda: JDA,
        lavalink: JdaLavalink
) {

    private val lavalinkStats = lavalink.nodes.associateWith { it.stats }

    val guildCount = Gauge
            .builder("bardybot_guild_count") { jda.guilds.size }
            .description("The amount of guilds that BardyBot is currently in")
            .baseUnit("guilds")
            .register(meterRegistry)

    val voiceConnections = Gauge
            .builder("bardybot_voice_connections") { jda.guilds.filter { it.selfMember.voiceState != null }.size }
            .description("The amount of voice channels BardyBot is currently connected to")
            .baseUnit("voice connections")
            .register(meterRegistry)
}