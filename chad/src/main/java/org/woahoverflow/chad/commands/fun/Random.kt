package org.woahoverflow.chad.commands.`fun`

import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder

import java.security.SecureRandom
import java.util.HashMap
import kotlin.random.Random

/**
 * Gets a variety of random things
 *
 * @author sho
 */
class Random : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        // Makes sure there's arguments
        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "random **number/quote/word**")
            return
        }

        when (args[0].toLowerCase()) {
            "number" -> {
                val rand = SecureRandom()

                // If the args size is 2, custom number was inputted
                if (args.size == 2) {
                    // Try block is to catch if the argument wasn't a number
                    try {
                        val i = Integer.parseInt(args[1])

                        // Makes sure the input isn't 0
                        if (i == 0) {
                            messageHandler.sendError("Cannot use 0!")
                            return
                        }

                        // Gets the random numbers and sends
                        messageHandler.sendEmbed(EmbedBuilder().withDesc("Your random number is `${rand.nextInt(i)}`. (out of `$i`)"))
                    } catch (throwaway: NumberFormatException) {
                        messageHandler.sendError("Invalid Number!")
                    }

                    return
                }

                // Sends a random number within 100
                messageHandler.sendEmbed { withDesc("Your random number is `${rand.nextInt(100)}`.") }
                return
            }
            "word" -> {
                val word = ChadVar.wordsList[Random.nextInt(ChadVar.wordsList.size)]

                // Makes the first letter of the word uppercase
                val uppercaseWord = word.toUpperCase()[0] + word.substring(1)

                messageHandler.sendEmbed { withDesc(uppercaseWord) }
                return
            }
            else -> messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "random [quote/word/number]")
        }
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["random number {max}"] = "Gives random number with an optional max value."
        st["random word"] = "Gets a random word."
        Command.helpCommand(st, "Random", e)
    }
}
