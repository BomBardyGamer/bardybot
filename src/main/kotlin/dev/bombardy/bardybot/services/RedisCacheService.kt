package dev.bombardy.bardybot.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service

@Service
class RedisCacheService @Autowired constructor(
        private val valueOperations: ValueOperations<Long, Long>
) {

    fun getVoiceChannel(guildId: Long) = valueOperations.get(guildId)

    fun putVoiceChannel(guildId: Long, channelId: Long) = valueOperations.set(guildId, channelId)
}