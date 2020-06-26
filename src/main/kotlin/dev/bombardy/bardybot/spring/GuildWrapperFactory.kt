package dev.bombardy.bardybot.spring

import dev.bombardy.bardybot.audio.MusicManager
import dev.bombardy.bardybot.getBean
import net.dv8tion.jda.api.entities.Guild
import org.springframework.beans.factory.BeanFactory
import org.springframework.stereotype.Component

/**
 * Factory class for creating [GuildWrapper] objects, used for creating
 * new guild wrappers from given Discord guilds.
 *
 * @author Callum Seabrook
 * @since 1.0
 */
@Component
class GuildWrapperFactory(private val beanFactory: BeanFactory) {

    fun create(guild: Guild) = GuildWrapper(guild, beanFactory.getBean())
}

/**
 * Wrapper class for Discord Guilds, including [MusicManager]
 */
data class GuildWrapper(val guild: Guild, val musicManager: MusicManager)