package me.bardy.bot.commands.music

import com.mojang.brigadier.tree.LiteralCommandNode
import me.bardy.bot.command.Command
import me.bardy.bot.command.CommandContext
import me.bardy.bot.components.ManagerMap
import org.springframework.stereotype.Component

@Component
class ClearQueueCommand(private val musicManagers: ManagerMap) : Command("cl, cq") {

    override fun register(): LiteralCommandNode<CommandContext> = default("clear") {
        val queue = musicManagers.get(it.guild.id).scheduler.queue
        if (queue.isEmpty()) {
            it.reply("I tried to clear the queue, but there wasn't any tracks to clear :pensive:")
            return@default
        }

        queue.clear()
        it.reply("Bang! And the tracks are... gone...")
    }
}
