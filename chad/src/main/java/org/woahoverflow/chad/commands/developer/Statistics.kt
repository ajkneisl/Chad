package org.woahoverflow.chad.commands.developer

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.util.*

/**
 * Gets statistics
 *
 * @author sho
 */
class Statistics : Command.Class {
    override fun run(e: MessageEvent, args: List<String>): Runnable {
        return Runnable {
            val guilds = RequestBuffer.request<List<IGuild>> {
                e.client.guilds
            }.get()

            var biggestGuild: IGuild? = null
            var biggestGuildSize = 0

            var users = 0

            for (guild in guilds) {
                users += guild.users.size

                if (guild.users.size > biggestGuildSize) {
                    biggestGuild = guild
                    biggestGuildSize = guild.users.size
                }
            }

            // Creates an EmbedBuilder and applies all the values
            val embedBuilder = EmbedBuilder()
            embedBuilder.withTitle("Statistics")
            embedBuilder.withDesc(
                    "Guilds `${guilds.size}`" +
                            "\n Users `$users`" +
                            "\n Biggest Guild `${biggestGuild!!.name}`" +
                            "\n Biggest Guild Size `$biggestGuildSize`"
            )

            // Sends the embed builder
            MessageHandler(e.channel, e.author).sendEmbed(embedBuilder)
        }
    }

    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["statistics"] = "Gets statistics about the bot."
        return Command.helpCommand(st, "Statistics", e)
    }
}