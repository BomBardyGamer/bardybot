package me.bardy.bot.components

import com.mojang.brigadier.CommandDispatcher
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import lavalink.client.io.jda.JdaLavalink
import me.bardy.bot.command.CommandContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

// responsible for registering various beans we need
@Component
class RegistrationComponent {

    @Bean
    fun audioPlayerManager(): AudioPlayerManager = DefaultAudioPlayerManager()

    @Bean
    fun commandDispatcher(): CommandDispatcher<CommandContext> = CommandDispatcher()

    @Bean
    fun musicManagers(lavalink: JdaLavalink): ManagerMap = ManagerMap(lavalink)
}
