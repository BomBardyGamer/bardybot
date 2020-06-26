package dev.bombardy.bardybot.spring

import dev.bombardy.bardybot.audio.MusicManager
import dev.bombardy.bardybot.getBean
import net.dv8tion.jda.api.entities.Guild
import org.springframework.beans.factory.BeanFactory
import org.springframework.stereotype.Component

@Component
class GuildWrapperFactory(private val beanFactory: BeanFactory) {

    fun create(guild: Guild) = GuildWrapper(guild, beanFactory.getBean())
}

data class GuildWrapper(val guild: Guild, val musicManager: MusicManager)