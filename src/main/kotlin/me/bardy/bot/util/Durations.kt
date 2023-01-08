package me.bardy.bot.util

import java.time.Duration

object Durations {

    /**
     * Formats the [Duration] in to a specific format.
     *
     * This format is either %d:%02d:%02d if this duration has an hour component greater than 0
     * Or it is %d:%02d if it does not
     *
     * @return the [Duration] in one of the formats above
     */
    @JvmStatic
    fun formatHumanReadable(duration: Duration): String {
        val hours = duration.toHours()
        if (hours > 0) return String.format("%d:%02d:%02d", hours, duration.toMinutesPart(), duration.toSecondsPart())
        return String.format("%d:%02d", duration.toMinutes(), duration.toSecondsPart())
    }

    @JvmStatic
    fun formatHumanReadable(durationMillis: Long): String = formatHumanReadable(Duration.ofMillis(durationMillis))
}