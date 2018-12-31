package org.woahoverflow.chad.commands.info

import org.woahoverflow.chad.framework.Util
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.handle.MessageHandler
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder

import java.lang.management.ManagementFactory
import java.util.HashMap

/**
 * @author sho
 * @since 0.6.3 B2
 */
class Chad : Command.Class {
    override fun run(e: MessageReceivedEvent, args: List<String>): Runnable {
        return Runnable {
            // Creates an embed builder, and adds links etc to it.
            val embedBuilder = EmbedBuilder()

            val desc = "__**Chad** by woahoverflow__\n\n" +
                    "**Uptime** ${Util.fancyDate(ManagementFactory.getRuntimeMXBean().uptime)}\n" +
                    "**Ping** ${e.client.shards[0].responseTime}ms\n" +
                    "**GitHub** https://woahoverflow.org/github\n" +
                    "**Website** http://woahoverflow.org/chad"

            embedBuilder.withDesc(desc)

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
