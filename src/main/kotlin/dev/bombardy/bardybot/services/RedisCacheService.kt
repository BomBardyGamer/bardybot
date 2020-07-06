package dev.bombardy.bardybot.services

import dev.bombardy.bardybot.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service

@Service
class RedisCacheService @Autowired constructor(
        private val valueOperations: ValueOperations<Long, Long>
) {

    fun getVoiceChannel(guildId: Long): Long? {
        LOGGER.debug("Attempting to retrieve cached channel id for guild with id $guildId")
        return valueOperations.get(guildId)
    }

    fun putVoiceChannel(guildId: Long, channelId: Long) {
        LOGGER.debug("Caching voice channel with id $channelId in guild with id $guildId")
        valueOperations.set(guildId, channelId)
    }

    fun removeVoiceChannel(guildId: Long): Long? {
        LOGGER.debug("Attempting to remove cached channel id for guild with id $guildId")
        val oldValue = valueOperations.get(guildId)
                ?: return null

        when (valueOperations.operations.delete(oldValue)) {
            true -> LOGGER.debug("Successfully removed cached channel id ($oldValue) for guild with id $guildId")
            else -> LOGGER.debug("Failed to remove cached channel id for guild with id $guildId")
        }

        return oldValue
    }

    companion object {
        private val LOGGER = getLogger<RedisCacheService>()
    }
}