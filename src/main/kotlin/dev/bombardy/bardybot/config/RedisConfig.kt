package dev.bombardy.bardybot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import kotlin.properties.Delegates

@Configuration
@ConfigurationProperties(prefix = "redis")
class RedisConfig {

    lateinit var hostname: String

    var port by Delegates.notNull<Int>()
}