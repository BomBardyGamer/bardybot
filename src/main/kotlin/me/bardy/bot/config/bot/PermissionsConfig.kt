package me.bardy.bot.config.bot

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("permissions")
@JvmRecord
data class PermissionsConfig(val enabled: Boolean, val roles: List<String>)