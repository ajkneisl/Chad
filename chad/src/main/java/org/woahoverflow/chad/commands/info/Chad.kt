package org.woahoverflow.chad.commands.info

import org.woahoverflow.chad.framework.Util
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder
import java.lang.management.ManagementFactory
import java.util.*

/**
 * Information about Chad
 *
 * @author sho
 */
class Chad : Command.Class {
    override fun run(e: MessageReceivedEvent, args: List<String>): Runnable {
        return Runnable {
            // Creates an embed builder, and adds links etc to it.
            val embedBuilder = EmbedBuilder()

            val desc = "**Chad** by woahoverflow\n\n" +
                    "**Uptime** ${Util.fancyDate(ManagementFactory.getRuntimeMXBean().uptime)}\n" +
                    "**Ping** `${e.client.shards[0].responseTime}` ms\n" +
                    "**GitHub** https://woahoverflow.org/github"

            embedBuilder.withDesc(desc)
            embedBuilder.withUrl("https://woahoverflow.org/chad")

            // Sends
            MessageHandler(e.channel, e.author).sendEmbed(embedBuilder)
        }
    }

    override fun help(e: MessageReceivedEvent): Runnable {
        val st = HashMap<String, String>()
        st["chad"] = "Gives information about the bot."
        return Command.helpCommand(st, "Chad", e)
    }
}
