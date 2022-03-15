package me.bardy.bot.util

import com.mojang.brigadier.context.CommandContext
import me.bardy.bot.command.CommandContext as BotCommandContext
import net.dv8tion.jda.api.entities.Member
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Color
import java.time.Duration

/**
 * Gets the SLF4J logger instance for the specified class [T] using the
 * [LoggerFactory.getLogger] method.
 *
 * @param T the class to log from
 * @return an instance of [Logger] for the specified class [T]
 */
inline fun <reified T> logger(): Logger = LogManager.getLogger(T::class.java)

inline fun <reified T> CommandContext<BotCommandContext>.argument(name: String): T = getArgument(name, T::class.java)

/**
 * Formats the [Duration] in to a specific format.
 *
 * This format is either %d:%02d:%02d if this duration has an hour component greater than 0
 * Or it is %d:%02d if it does not
 *
 * @return the [Duration] in one of the formats above
 */
fun Duration.format(): String {
    if (toHours() > 0) {
        return String.format("%d:%02d:%02d", toHours(), toMinutesPart(), toSecondsPart())
    }
    return String.format("%d:%02d", toMinutes(), toSecondsPart())
}

fun Member.formatName(): String {
    if (nickname == null) return user.asTag
    return "${user.asTag} (also known as $nickname)"
}

object Colors {

    val BARDY_ORANGE: Color = Color(255, 102, 0)
}
