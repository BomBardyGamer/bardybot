package me.bardy.bot.components

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
@EnableCaching
class CacheComponent {

    @Bean
    fun caffeine() = Caffeine.newBuilder()
        .maximumSize(128)
        .expireAfterWrite(1, TimeUnit.HOURS)

    @Bean
    fun cacheManager(caffeine: Caffeine<Any, Any>) = CaffeineCacheManager("parse_results")
        .apply { setCaffeine(caffeine) }
}