package org.woahoverflow.chad.commands.`fun`

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder

import java.util.HashMap

/**
 * Reverses a word
 *
 * @author sho
 */
class WordReverse : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        // Makes sure the arguments aren't empty
        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX).toString() + "upvote [@user]")
            return
        }

        // Gets the word from all the arguments
        val stringBuilder = StringBuilder()
        for (s in args)
            stringBuilder.append("$s ")

        // Gets the word & sends
        val word = stringBuilder.toString().trim { it <= ' ' }
        messageHandler.sendEmbed(EmbedBuilder().withDesc("Word: `" + word + "`\n`" + stringBuilder.reverse().toString().trim { it <= ' ' } + '`'.toString()))
    }

    override suspend fun help(e: MessageEvent) {
        val hash = HashMap<String, String>()
        hash["wr [word]"] = "Reverses a word."
        Command.helpCommand(hash, "Word Reverse", e)
    }
}
