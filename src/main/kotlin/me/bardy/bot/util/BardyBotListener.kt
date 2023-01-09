package me.bardy.bot.util

import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * Marker class for Spring to be able to inject a set of these without
 * injecting all listener adapters.
 */
abstract class BardyBotListener : ListenerAdapter()
