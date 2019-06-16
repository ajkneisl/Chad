package dev.shog.chad.commands.`fun`

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
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

        // Checks if there's arguments
        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "rps [rock/paper/scissors]", includePrefix = true)
            return
        }

        // Makes sure the arguments are rock, paper and scissors
        if (Stream.of("rock", "paper", "scissors").anyMatch { s -> args[0].equals(s, ignoreCase = true) }) {
            // Gets Chad's value
            val i2 = SecureRandom().nextInt(3)

            // Forms the author's value
            val i = when (args[0].toLowerCase()) {
                "rock" -> 0
                "paper" -> 1
                "scissors" -> 2
                else -> 2
            }

            // Sends the result
            messageHandler.sendEmbed { withDesc(calculateWinner(i, i2)) }
        } else {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "rps [rock/paper/scissors]", includePrefix = true)
        }
    }

    /**
     * Builds the string.
     *
     *
     */
    private fun calculateWinner(i: Int, i2: Int): String {
        val chadValue: String = when (i2) {
            0 -> "Rock"
            1 -> "Paper"
            2 -> "Scissors"
            else -> return "Internal Exception!"
        }

        return when {
            i2 == i -> "Chad had `$chadValue`, tie!"
            i2 == 2 && i == 1 || i2 == 0 && i == 2 || i2 == 1 && i == 0 -> "Chad had `$chadValue`, Chad wins!"
            else -> "Chad had `$chadValue`, you win!"
        }
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["rps [rock/paper/scissors]"] = "Plays rock paper scissors with Chad."
        Command.helpCommand(st, "Rock Paper Scissors", e)
    }
}
