package dev.bombardy.bardybot.components

import dev.bombardy.bardybot.config.RedisConfig
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisCacheComponent {

    @Bean
    fun redisTemplate(connectionFactory: LettuceConnectionFactory, config: RedisConfig): RedisTemplate<Long, Long> {
        connectionFactory.standaloneConfiguration.apply {
            hostName = config.hostname
            port = config.port
        }

        return RedisTemplate<Long, Long>().apply { setConnectionFactory(connectionFactory) }
    }


    @Bean
    fun valueOperations(redisTemplate: RedisTemplate<Long, Long>) = redisTemplate.opsForValue()
}