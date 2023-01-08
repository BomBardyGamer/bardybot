package me.bardy.bot.config

import java.net.URI
import lavalink.client.io.jda.JdaLavalink
import me.bardy.bot.config.bot.BotConfig
import me.bardy.bot.config.bot.LavalinkConfig
import me.bardy.bot.util.GuildMusicManagers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MusicConfig {

    @Bean
    fun lavalink(linkConfig: LavalinkConfig, botConfig: BotConfig): JdaLavalink {
        val lavalink = JdaLavalink(botConfig.clientId, 1, null)
        linkConfig.nodes.forEach { lavalink.addNode(it.name, URI(it.url), it.password) }
        return lavalink
    }

    @Bean
    fun managerMap(lavalink: JdaLavalink): GuildMusicManagers = GuildMusicManagers(lavalink)
}
