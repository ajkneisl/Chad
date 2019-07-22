package dev.shog.chad.commands.`fun`

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.uno.obj.Card
import dev.shog.chad.framework.handle.uno.obj.CardColor
import dev.shog.chad.framework.handle.uno.obj.CardType
import dev.shog.chad.framework.handle.uno.obj.UnoGame
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.HashMap


/**
 * Play Uno
 *
 * @author sho
 */
class Uno : Command.Class {
    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["uno"] = "Starts a game of Uno, or gets your cards."
        st["uno end"] = "Ends a game of Uno."
        st["uno play **number**"] = "Plays the card."
        st["!TEXT!How to Play"] = "Start of a game of Uno with just `!PREFIX!uno`. This will give you 7 cards, and Chad 7 cards also. It plays down a card, and you must play off that. When you have one card, before playing next, you must type `!PREFIX!uno call`"
        Command.helpCommand(st, "Uno", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        if (args.size >= 1) {
            when (args[0].toLowerCase()) {
                // When the user tries to end the game
                "end" -> {
                    if (!UnoGame.games.containsKey(e.author)) {
                        messageHandler.sendError("You haven't created a game!")
                        return
                    }

                    messageHandler.sendMessage("Ended game!")
                    UnoGame.games.remove(e.author)
                    return
                }

                "call" -> {
                    if (!UnoGame.games.containsKey(e.author)) {
                        messageHandler.sendError("You haven't created a game!")
                        return
                    }

                    val game = UnoGame.getGame(e.author)
                    val uno = game.second

                    if (uno.userCards.size == 1) {
                        uno.userCalledUno = true
                        messageHandler.sendMessage("You have called uno!")
                        return
                    } else {
                        messageHandler.sendError("You don't have 1 card left!")
                    }
                }

                // When the user draws a card
                "draw" -> {
                    val game = UnoGame.getGame(e.author)
                    val uno = game.second

                    if (game.first) {
                        messageHandler.sendMessage("You haven't seen your cards set! Use `${prefix}uno` first!")
                        UnoGame.games.remove(e.author)
                        return
                    }

                    messageHandler.sendEmbed {
                        val drawn = uno.drawCard()
                        uno.userCards.add(drawn)

                        // The user's current cards.
                        appendDesc("Chad has ${uno.botCards.size} cards\n\n")
                        appendDesc("Your current cards:\n\n")
                        for (i in uno.userCards.indices) {
                            val crd = uno.userCards[i]
                            appendDesc("**${i + 1}**: $crd\n")
                        }

                        // Gives the output of the drawn card.
                        appendDesc("\nYou drew a $drawn.")

                        // The most recent played card
                        appendDesc("\n\nThe most recently played card is a ${uno.playedCards.last()}")
                    }

                    return
                }

                "play" -> {
                    if (args.size < 2) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "uno play [number]", includePrefix = true)
                        return
                    }

                    val game = UnoGame.getGame(e.author)
                    val uno = game.second

                    // If they haven't initialized the Uno game yet
                    if (game.first) {
                        messageHandler.sendMessage("You haven't seen your cards set! Use `${prefix}uno` first!")
                        UnoGame.games.remove(e.author)
                        return
                    }

                    // Makes sure they inputted a valid number
                    val number = args[1].toIntOrNull()?.minus(1) ?: run {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "uno play [number]", includePrefix = true)
                        return
                    }

                    // If they've got 1 card, and they haven't called uno, give them 2 cards.
                    if (uno.userCards.size == 1 && !uno.userCalledUno) {
                        messageHandler.sendError("You didn't call Uno! You have gained 2 cards!")

                        uno.userCards.add(uno.drawCard())
                        uno.userCards.add(uno.drawCard())

                        return
                    }

                    // Attempts to play the card
                    var card = try {
                        uno.userCards[number]
                    } catch (e: Exception) {
                        messageHandler.sendError("That card doesn't exist!")
                        return
                    }

                    // The wild color
                    if (card.color == CardColor.WILD && args.size != 3) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "uno play [number] [red/green/blue/yellow]", includePrefix = true)
                        return
                    } else if (args.size == 3 && card.color == CardColor.WILD) {
                        val color = when (args[2].toLowerCase()) {
                            "green" -> CardColor.GREEN
                            "yellow" -> CardColor.YELLOW
                            "blue" -> CardColor.BLUE
                            "red" -> CardColor.RED
                            else -> {
                                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "uno play [number] [red/green/blue/yellow]", includePrefix = true)
                                return
                            }
                        }

                        uno.userCards.remove(card)
                        card = Card(color, card.type, null)
                        uno.userCards.add(card)
                    }

                    val playedCard = uno.playerPlayCard(card)
                    if (!playedCard.first) {
                        messageHandler.sendError("You can't play that card!")
                        return
                    }

                    if (uno.userCards.size == 0) {
                        messageHandler.sendEmbed { withDesc("You won!") }
                        uno.endGame(true)
                        return
                    }

                    // Sends the user's updated status.
                    messageHandler.sendEmbed {
                        // The user's new cards.
                        appendDesc("Chad has ${uno.botCards.size} cards\n\n")
                        appendDesc("Your current cards:\n\n")
                        for (i in uno.userCards.indices) {
                            val crd = uno.userCards[i]
                            appendDesc("**${i + 1}**: $crd\n")
                        }

                        // Gives the output of the played card.
                        appendDesc("\nYou played a $card.")

                        if (playedCard.second) {
                            appendDesc("\n\nYou skipped Chad's turn!")
                            return@sendEmbed
                        }

                        uno.chadPlayCard().also {
                            appendDesc("\n\nChad played a ${it.first}.")

                            var cont = it.second

                            while (cont) {
                                val ret = uno.chadPlayCard()
                                appendDesc("\nYour turn was skipped, so Chad played a ${ret.first}")
                                cont = ret.second
                            }
                        }

                        if (uno.botCards.size == 0) {
                            withDesc("Chad won!")
                        }
                    }
                    return
                }
            }
        }

        val game = UnoGame.getGame(e.author)
        val uno = game.second

        messageHandler.sendEmbed {
            if (game.first) {
                withDesc("You have started with these cards. Play your first card by using the number on the left of the card, then typing `${prefix}uno play {num}`!" +
                        "\nMake sure that if you have 1 card left, to do `${prefix}uno call`." +
                        "\n\n")
            }

            if (game.first) {
                val init = uno.initGame()
                appendDesc("The first played card is a $init\n\n")
            }

            if (!game.first) {
                appendDesc("Chad has ${uno.botCards.size} cards\n\n")
            }

            for (i in uno.userCards.indices) {
                val card = uno.userCards[i]
                appendDesc("**${i + 1}**: $card\n")
            }

            if (!game.first) {
                appendDesc("\nThe last played card is: ${uno.playedCards.last()}")
            }
        }
    }
}
