package me.bardy.bot.config

import dev.arbjerg.lavalink.client.LavalinkClient
import dev.arbjerg.lavalink.client.NodeOptions
import dev.arbjerg.lavalink.client.getUserIdFromToken
import me.bardy.bot.config.bot.BotConfig
import me.bardy.bot.config.bot.LavalinkConfig
import me.bardy.bot.audio.GuildMusicManagers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MusicConfiguration {

    @Bean
    fun lavalink(linkConfig: LavalinkConfig, botConfig: BotConfig): LavalinkClient {
        val lavalink = LavalinkClient(getUserIdFromToken(botConfig.token))
        linkConfig.nodes.forEach {
            val node = NodeOptions.Builder()
                .setName(it.name)
                .setServerUri(it.url)
                .setPassword(it.password)
                .build()
            lavalink.addNode(node)
        }
        return lavalink
    }

    @Bean
    fun managerMap(lavalink: LavalinkClient): GuildMusicManagers = GuildMusicManagers(lavalink)
}
