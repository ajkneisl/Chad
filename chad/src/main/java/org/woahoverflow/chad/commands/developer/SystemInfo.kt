package org.woahoverflow.chad.commands.developer

import com.sun.management.OperatingSystemMXBean
import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.LAUNCH_ARGUMENTS
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.lang.management.ManagementFactory
import java.util.*

/**
 * Gives info about the system via discord
 *
 * @author sho, codebasepw
 */
class SystemInfo : Command.Class {
    override fun run(e: MessageEvent, args: List<String>): Runnable {
        return Runnable {
            // Gets the OperatingSystemMXBean
            val os = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean

            // uaaaaaaa
            val stringBuilder = StringBuilder()

            for (arg in LAUNCH_ARGUMENTS) if (arg.value) stringBuilder.append("`").append(arg.key).append("`, ")

            // Creates an EmbedBuilder and applies all the values
            val embedBuilder = EmbedBuilder()
            embedBuilder.withTitle("System Information")
            embedBuilder.withDesc(
                    "OS `" + os.name + " [" + os.version + "]`" +
                            "\n Available cores `" + os.availableProcessors + '`'.toString() +
                            "\n Home Directory `${JsonHandler.fileLocation.path}" +
                            "\n CPU Load `" + os.systemCpuLoad + '`'.toString() +
                            "\n Memory `" + os.totalPhysicalMemorySize / 1000 / 1000 + "`mb" +
                            "\n Shard Response Time `" + e.author.shard.responseTime + "`ms" +
                            "\n Java Version `" + System.getProperty("java.version") + "`" +
                            "\n Chad Version `${ChadVar.VERSION}`" +
                            "\n Launch Arguments ${stringBuilder.toString().removeSuffix(", ")}"
            )

            // Sends the embed builder
            MessageHandler(e.channel, e.author).sendEmbed(embedBuilder)
        }
    }

    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["systeminfo"] = "Displays system/connectivity information about the bot."
        return Command.helpCommand(st, "System Information", e)
    }
}
