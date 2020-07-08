package dev.bombardy.bardybot

import net.dv8tion.jda.api.JDA
import java.util.function.Function

class JDAFunction : Function<Int, JDA> {

    lateinit var jda: JDA

    override fun apply(shardId: Int) = jda
}