package org.woahoverflow.chad.commands.developer

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.sync.sync
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Ability to sync with other applications through Discord
 *
 * @author sho
 */
class Sync : Command.Class {
    override fun help(e: MessageReceivedEvent): Runnable {
        val hash = HashMap<String, String>()
        hash["sync"] = "Syncs with other applications."
        return Command.helpCommand(hash, "Sync", e)
    }

    override fun run(e: MessageReceivedEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val messageHandler = MessageHandler(e.channel, e.author)
            val prefix = GuildHandler.handle.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

            if (args.size == 0) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}sync <website>")
                return@Runnable
            }

            when (args[0].toLowerCase()) {
                "website" -> {
                    sync(e.client)

                    messageHandler.sendEmbed(EmbedBuilder().withDesc("Successfully synced with website!"))

                    return@Runnable
                }

                else -> {
                    messageHandler.sendEmbed(EmbedBuilder().withDesc("Invalid sync option!"))

                    return@Runnable
                }
            }
        }
    }

}