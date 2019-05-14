package org.woahoverflow.chad.commands.`fun`

import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder

import java.security.SecureRandom
import java.util.HashMap
import java.util.stream.Stream

/**
 * Play Rock Paper Scissors with Chad
 *
 * @author sho
 */
class RockPaperScissors : Command.Class {
    override suspend fun run(e: MessageEvent, args:MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        // Checks if there's arguments
        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "rps [rock/paper/scissors]")
            return
        }

        // Makes sure the arguments are rock, paper and scissors
        if (Stream.of("rock", "paper", "scissors").anyMatch { s -> args[0].equals(s, ignoreCase = true) }) {
            // Gets Chad's value
            val i2 = SecureRandom().nextInt(3)

            // Forms the author's value
            var i = 420
            if (args[0].equals("rock", ignoreCase = true))
                i = 0
            else if (args[0].equals("paper", ignoreCase = true))
                i = 1
            else if (args[0].equals("scissors", ignoreCase = true))
                i = 2

            // Sends the result
            messageHandler.sendEmbed(EmbedBuilder().withDesc(calculateWinner(i, i2)))
        } else {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "rps [rock/paper/scissors]")
        }
    }

    // Builds the string for RPS
    private fun calculateWinner(i: Int, i2: Int): String {
        // 'i' is meant for the user's input
        // 'i2' is meant for the bot's input

        // Builds Chad's value
        val chadValue: String
        when (i2) {
            0 -> chadValue = "Rock"
            1 -> chadValue = "Paper"
            2 -> chadValue = "Scissors"
            else -> return "Internal Exception!"
        }

        // If they're both equal, tie
        if (i2 == i) {
            return "Chad had `$chadValue`, tie!"
        }

        // If Chad has scissors and author has paper
        if (i2 == 2 && i == 1) {
            return "Chad had `$chadValue`, Chad wins!"
        }

        // If Chad has rock and the author has scissors
        if (i2 == 0 && i == 2) {
            return "Chad had `$chadValue`, Chad wins!"
        }

        // If Chad has Paper and the author has rock
        return if (i2 == 1 && i == 0) {
            "Chad had `$chadValue`, Chad wins!"
        } else "Chad had `$chadValue`, you win!"

        // The rest of the inputs are the author winning, so
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["rps [rock/paper/scissors]"] = "Plays rock paper scissors with Chad."
        Command.helpCommand(st, "Rock Paper Scissors", e)
    }
}
