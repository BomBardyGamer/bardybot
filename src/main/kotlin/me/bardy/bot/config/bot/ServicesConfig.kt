package me.bardy.bot.config.bot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("services")
@ConstructorBinding
data class ServicesConfig(val metrics: Boolean, val api: Boolean)
