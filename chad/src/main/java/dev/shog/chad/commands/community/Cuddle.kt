package dev.shog.chad.commands.community

import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * I don't know what's wrong with command.
 *
 * @author sho
 */
class Cuddle : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        if (e.message.mentions.isEmpty()) {
            messageHandler.sendEmbed { withDesc("You cuddled with yourself, how nice.") }
            return
        }

        // Target for the command
        val target = e.message.mentions[0]

        messageHandler.sendEmbed {
            // Decides what type of user the target is.
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
