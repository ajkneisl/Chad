package org.woahoverflow.chad.commands.info

import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.util.Util
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
        // Creates an embed builder, and adds links etc to it.
        val embedBuilder = EmbedBuilder()

        val desc = "**Chad** by woahoverflow\n\n" +
                "**Uptime** ${Util.fancyDate(ManagementFactory.getRuntimeMXBean().uptime)}\n" +
                "**Ping** `${e.client.shards[0].responseTime}` ms\n" +
                "**GitHub** https://woahoverflow.org/github\n\nIf there's an issue, or something needs to be fixed, message shozer#0001 on Discord."

        embedBuilder.withDesc(desc)
        embedBuilder.withUrl("https://woahoverflow.org/chad")

        // Sends
        MessageHandler(e.channel, e.author).credit(ChadVar.VERSION).sendEmbed(embedBuilder)
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["chad"] = "Gives information about the bot."
        Command.helpCommand(st, "Chad", e)
    }
}
