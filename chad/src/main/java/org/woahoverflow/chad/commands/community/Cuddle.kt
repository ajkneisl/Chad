package org.woahoverflow.chad.commands.community

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Why not?
 *
 * @author codebasepw
 */
class Cuddle : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        if (e.message.mentions.isEmpty()) {
            messageHandler.sendEmbed { withDesc("You cuddled with yourself, how nice.") }
            return
        }

        val target = e.message.mentions[0]



        messageHandler.sendEmbed {
            when (target) {
                e.author -> withDesc("You cuddled with yourself, how nice.")
                e.client.ourUser -> withDesc("You cuddled with `${target.name}` with consent, because `${target.name}` loves you.")
                else -> withDesc("You cuddled with `" + target.name + "` without direct consent, my man <:squadW:579798723467411494>")
            }
        }
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["cuddle [@user]"] = "Cuddle with another user."
        st["cuddle"] = "Cuddle with yourself. :)"
        Command.helpCommand(st, "Cuddle", e)
    }
}
