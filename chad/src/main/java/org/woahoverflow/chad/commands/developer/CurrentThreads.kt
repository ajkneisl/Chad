package org.woahoverflow.chad.commands.developer

import com.sun.management.OperatingSystemMXBean
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.runningThreads
import org.woahoverflow.chad.framework.handle.threadHash
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.lang.management.ManagementFactory
import java.util.*

/**
 * The current running threads within Chad
 *
 * @author sho, codebasepw
 */
class CurrentThreads : Command.Class {
    override fun run(e: MessageEvent, args: List<String>): Runnable {
        return Runnable {
            // Creates an embed builder and applies a title
            val embedBuilder = EmbedBuilder()
            embedBuilder.withTitle("Current Threads Running")

            // Adds all threads running threads to the stringbuilder, than to the description.
            val stringBuilder = StringBuilder()
            threadHash.forEach { key, `val` ->
                stringBuilder.append('`')
                        .append(key)
                        .append("`: ")
                        .append(`val`.size)
                        .append('\n')
            }

            // Gets the used ram by the JVM, and the available ram and adds it to the stringbuilder
            stringBuilder.append("\nThe JVM is currently using `")
                    .append(Runtime.getRuntime().totalMemory() / 1000 / 1000).append("`mb out of `")
                    .append(
                            (ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean).totalPhysicalMemorySize / 1000 / 1000
                    ).append("`mb.")

            // Get the internal and user run threads
            stringBuilder.append("\n\nThere's currently `").append(runningThreads).append("` threads running.")

            // Append to builder
            embedBuilder.appendDesc(stringBuilder.toString())

            // Sends
            MessageHandler(e.channel, e.author).sendEmbed(embedBuilder)
        }
    }

    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["threads"] = "Displays all running threads for users."
        return Command.helpCommand(st, "Current Threads", e)
    }

}
