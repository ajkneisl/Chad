package dev.shog.chad.commands.info

import dev.shog.chad.core.ChadVar
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.util.Util
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.lang.management.ManagementFactory
import java.util.*

/**
 * Information about Chad
 *
 * @author sho
 */
class Chad : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        MessageHandler(e.channel, e.author).credit(ChadVar.VERSION).sendEmbed {
            withDesc("**Chad** by sho\n\n" +
                    "**Uptime** ${Util.fancyDate(ManagementFactory.getRuntimeMXBean().uptime)}\n" +
                    "**Ping** `${e.client.shards[0].responseTime}` ms\n" +
                    "**GitHub** https://github.com/shoganeko/chad" +
                    "\n\nIf there's an issue, please PM me on discord: `SHO#0001`")
            withUrl("https://github.com/shoganeko/chad")
        }
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["chad"] = "Gives information about the bot."
        Command.helpCommand(st, "Chad", e)
    }
}