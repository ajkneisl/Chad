package org.woahoverflow.chad.commands.admin

import com.sun.management.OperatingSystemMXBean
import org.woahoverflow.chad.framework.Chad
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder
import java.lang.management.ManagementFactory
import java.util.*

/**
 * The current running threads within Chad
 *
 * @author sho, codebasepw
 */
class CurrentThreads : Command.Class {
    override fun run(e: MessageReceivedEvent, args: List<String>): Runnable {
        return Runnable {
            // Creates an embed builder and applies a title
            val embedBuilder = EmbedBuilder()
            embedBuilder.withTitle("Current Threads Running")

            // Adds all threads running threads to the stringbuilder, than to the description.
            val stringBuilder = StringBuilder()
            Chad.threadHash.forEach { key, `val` ->
                if (key.isDiscordUser) {
                    stringBuilder.append('`')
                            .append(key.userId)
                            .append("`: ")
                            .append(`val`.size)
                            .append('\n')
                }
            }

            // Gets the used ram by the JVM, and the available ram and adds it to the stringbuilder
            stringBuilder.append("\nThe JVM is currently using `")
                    .append(Runtime.getRuntime().totalMemory() / 1000 / 1000).append("`mb out of `")
                    .append(
                            (ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean).totalPhysicalMemorySize / 1000 / 1000
                    ).append("`mb.")

            // Get the internal and user run threads
            stringBuilder.append("\n\nThere's currently `").append(Chad.internalRunningThreads)
                    .append("` internal thread(s) running, there's currently `")
                    .append(Chad.runningThreads).append("` user run threads.")

            // Append to builder
            embedBuilder.appendDesc(stringBuilder.toString())

            // Sends
            MessageHandler(e.channel, e.author).sendEmbed(embedBuilder)
        }
    }

    override fun help(e: MessageReceivedEvent): Runnable {
        val st = HashMap<String, String>()
        st["threads"] = "Displays all running threads for users."
        return Command.helpCommand(st, "Current Threads", e)
    }

}
