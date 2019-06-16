package dev.shog.chad.commands.`fun`

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder

import java.util.HashMap
import java.util.stream.Collectors

/**
 * Reverses a word
 *
 * @author sho
 */
class WordReverse : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)

        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "upvote [@user]", includePrefix = true)
            return
        }

        val built = args.stream().collect(Collectors.joining(" ")).trim()

        messageHandler.sendEmbed { withDesc("Word: `$built`\n`${built.trim().reversed()}`") }
    }

    override suspend fun help(e: MessageEvent) {
        val hash = HashMap<String, String>()
        hash["wr [word]"] = "Reverses a word."
        Command.helpCommand(hash, "Word Reverse", e)
    }
}
