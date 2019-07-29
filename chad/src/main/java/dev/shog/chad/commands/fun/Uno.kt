package dev.shog.chad.commands.`fun`

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.coroutine.asBoolean
import dev.shog.chad.framework.handle.coroutine.request
import dev.shog.chad.framework.handle.uno.handle.ChadAI
import dev.shog.chad.framework.handle.uno.obj.Card
import dev.shog.chad.framework.handle.uno.obj.CardColor
import dev.shog.chad.framework.handle.uno.obj.CardType
import dev.shog.chad.framework.handle.uno.obj.UnoGame
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import dev.shog.chad.framework.util.Util
import kotlinx.coroutines.delay
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.impl.obj.ReactionEmoji
import sx.blah.discord.util.RequestBuilder
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

                // When the user calls Uno
                "call" -> {
                    if (!UnoGame.games.containsKey(e.author)) {
                        messageHandler.sendError("You haven't created a game!")
                        return
                    }

                    val game = UnoGame.getGame(e.author)
                    val uno = game.second

                    // Makes sure they've got 1 card left, then call Uno
                    if (uno.user.cards.getSize() == 1) {
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
                        // Draws a card, and makes sure it returned properly
                        val drawn = uno.user.draw(1).also {
                            if (it.size <= 0) throw Exception("There was an issue drawing!")
                        }

                        // The user's current cards.
                        appendField("Chad", "${uno.chad.cards.getSize()} cards", true)
                        appendField("Last Played Card", uno.playedCards.last().toString(), true)
                        appendField("Drawn Card", drawn[0].toString(), true)

                        // Current cards
                        val builder = StringBuilder()
                        for (i in uno.user.cards.cards.indices) {
                            val card = uno.user.cards.cards[i]
                            builder.append("**${i + 1}**: $card\n")
                        }
                        appendField("Your Cards", builder.toString(), false)
                    }

                    return
                }

                // Plays a card
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
                    if (uno.user.cards.getSize() == 1 && !uno.userCalledUno) {
                        // Draws 2 cards and makes sure it was properly returned
                        val drawn = uno.user.draw(1).also {
                            if (it.size != 1) throw Exception("There was an issue drawing!")
                        }

                        messageHandler.sendError("You didn't call Uno! You have been given a ${drawn[0]} and ${drawn[1]}!")
                        return
                    }

                    // Attempts to play the card
                    var card = try {
                        uno.user.cards.cards[number]
                    } catch (e: Exception) {
                        messageHandler.sendError("That card doesn't exist!")
                        return
                    }

                    // The wild color
                    if (card.type == CardType.WILD_DEFAULT || card.type == CardType.WILD_DRAW) {
                        val msg = messageHandler.sendMessage("What color for the wild card?")!!

                        // Add reactions
                        val builder = RequestBuilder(e.client)
                        builder.shouldBufferRequests(true)

                        builder.doAction {
                            msg.addReaction(ReactionEmoji.of("\uD83D\uDCD8"))
                            return@doAction true
                        }.andThen {
                            msg.addReaction(ReactionEmoji.of("\uD83D\uDCD5"))
                            return@andThen true
                        }.andThen {
                            msg.addReaction(ReactionEmoji.of("\uD83D\uDCD9"))
                            return@andThen true
                        }.andThen {
                            msg.addReaction(ReactionEmoji.of("\uD83D\uDCD7"))
                            return@andThen true
                        }.execute()

                        var sec = 0
                        var selectedColor: CardColor? = null

                        while (selectedColor == null) {
                            if (sec >= 10) {
                                request { msg.edit("Timed out!") }
                                return
                            }

                            sec++

                            when {
                                // Blue
                                request {
                                    msg.getReactionByUnicode("\uD83D\uDCD8").getUserReacted(e.author)
                                }.asBoolean() -> selectedColor = CardColor.BLUE

                                // Red
                                request {
                                    msg.getReactionByUnicode("\uD83D\uDCD5").getUserReacted(e.author)
                                }.asBoolean() -> selectedColor = CardColor.RED

                                // Yellow
                                request {
                                    msg.getReactionByUnicode("\uD83D\uDCD9").getUserReacted(e.author)
                                }.asBoolean() -> selectedColor = CardColor.YELLOW

                                // Green
                                request {
                                    msg.getReactionByUnicode("\uD83D\uDCD7").getUserReacted(e.author)
                                }.asBoolean() -> selectedColor = CardColor.GREEN
                            }

                            delay(500L)
                        }

                        request { msg.delete() }
                        card = Card(selectedColor, card.type, null)
                    }

                    val playedCard = uno.user.play(card)
                    if (!playedCard.first) {
                        messageHandler.sendError("You can't play that card!")
                        return
                    }

                    // If they've got 0 cards, they won. Uno calling was previously checked
                    if (uno.user.cards.getSize() == 0) {
                        messageHandler.sendEmbed { withDesc("You won!") }
                        uno.endGame(true)
                        return
                    }

                    // Sends the user's updated status.
                    messageHandler.sendEmbed {
                        // Gives the output of the played card.
                        appendDesc(":black_small_square: You played a $card.")

                        // If you skipped Chad's turn
                        if (playedCard.second) {
                            appendDesc("\n:black_small_square: You skipped Chad's turn!")
                        } else {
                            // If not, play for Chad
                            ChadAI(uno).play().also {
                                val played = uno.chad.play(it)
                                appendDesc("\n:black_small_square: Chad played a $it.")

                                var cont = played.second

                                while (cont) {
                                    val c = ChadAI(uno).play()
                                    val ret = uno.chad.play(c)
                                    appendDesc("\n:black_small_square: Your turn was skipped, so Chad played a $c")
                                    cont = ret.second
                                }
                            }
                        }

                        // Chad's cards.
                        appendField("Chad", "${uno.chad.cards.getSize()} cards", true)

                        appendField("Last Played Card", uno.playedCards.last().toString(), true)
                        val builder = StringBuilder()
                        for (i in uno.user.cards.cards.indices) {
                            val cList = uno.user.cards.cards[i]
                            builder.append("**${i + 1}**: $cList\n")
                        }
                        appendField("Your Cards", builder.toString(), false)

                        // Chad has no more cards. Chad doesn't call Uno because that's just useless.
                        if (uno.chad.cards.getSize() == 0) {
                            clearFields()
                            withDesc("Chad won!")
                            uno.endGame(false)
                        }
                    }
                    return
                }
            }
        }

        val game = UnoGame.getGame(e.author)
        val uno = game.second

        // Gives info about the game & the user's cards
        messageHandler.sendEmbed {
            if (game.first) {
                withDesc("You have started a game of Uno!" +
                        "\nSelect a playable card below, and play it with `${prefix}uno play **number**`." +
                        "\nOnce you're about to play your last card make sure to type `${prefix}uno call`.")

                val init = uno.initGame()
                appendField("First Played Card", init.toString(), true)
            }

            if (!game.first) {
                withDesc("You started this game ${Util.fancyDate(System.currentTimeMillis() - uno.startedAt)} ago.")
                appendField("Chad", "${uno.chad.cards.getSize()} cards", true)
                appendField("Last Played Card", uno.playedCards.last().toString(), true)
            }

            val builder = StringBuilder()
            for (i in uno.user.cards.cards.indices) {
                val card = uno.user.cards.cards[i]
                builder.append("**${i + 1}**: $card\n")
            }
            appendField("Your Cards", builder.toString(), false)
        }
    }
}
