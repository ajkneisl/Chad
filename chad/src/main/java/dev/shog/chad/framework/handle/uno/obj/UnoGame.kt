package dev.shog.chad.framework.handle.uno.obj

import sx.blah.discord.handle.obj.IUser
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

/**
 * The main Uno game. This holds all data, including the played [Card]s etc.
 *
 * @param user The user who created the game instance.
 */
abstract class UnoGame(val user: IUser) {
    /**
     * If the game has ended
     */
    var isComplete = false

    /**
     * The bot's current cards.
     */
    val botCards = ArrayList<Card>()

    /**
     * The cards the user has.
     */
    val userCards = ArrayList<Card>()

    /**
     * A card that can possibly be used. [num] set to null when wild.
     */
    data class PreCard(val cardType: CardType, val cardColor: CardColor, val num: Int?)

    /**
     * The amount of times a [Card] has been used.
     */
    val usedCards = ConcurrentHashMap<PreCard, Int>()

    /**
     * The played [Card]s.
     */
    val playedCards = ArrayList<Card>()

    /**
     * Ends the game after a user runs out of cards.
     *
     * If the [userWon], this will be true.
     */
    fun endGame(userWon: Boolean) {
        isComplete = true
        games.remove(user)
    }


    /**
     * Plays a card, and checks if the user can.
     *
     * @param card The card the user is trying to play.
     * @return First: is okay; Second: Turn Skipped
     */
    fun playerPlayCard(card: Card): Pair<Boolean, Boolean> {
        if (!userCards.contains(card) || !canPlayCard(card)) return Pair(first = false, second = false)

        var turnSkipped = false

        when (card.type) {
            CardType.WILD_DEFAULT -> {}

            CardType.WILD_DRAW -> {
                turnSkipped = true
                repeat(4) {
                    botCards.add(drawCard())
                }
            }

            CardType.SKIP -> {
                turnSkipped = true
            }

            // Reverse does nothing
            CardType.REVERSE -> {}

            CardType.DRAW_TWO -> {
                turnSkipped = true
                repeat(2) {
                    botCards.add(drawCard())
                }
            }

            CardType.REGULAR -> {}
        }

        userCards.remove(card)
        playedCards.add(card)

        return Pair(true, turnSkipped)
    }

    /**
     * Plays a card.
     *
     * @return Card: The played card; Second: Turn Skipped
     */
    fun chadPlayCard(): Pair<Card, Boolean> {
        val card = ChadAI(this).play()

        if (!botCards.contains(card) || !canPlayCard(card)) throw IllegalArgumentException("Chad cannot play this card! ${playedCards.last()} -> $card")

        var turnSkipped = false

        when (card.type) {
            CardType.WILD_DRAW -> {
                turnSkipped = true
                repeat(4) {
                    botCards.add(drawCard())
                }
            }

            CardType.SKIP -> {
                turnSkipped = true
            }

            CardType.DRAW_TWO -> {
                turnSkipped = true
                repeat(2) {
                    userCards.add(drawCard())
                }
            }

            else -> {}
        }

        println("Played card: $card\nRemoved card: $card")

        botCards.remove(card)
        playedCards.add(card)

        return Pair(card, turnSkipped)
    }

    /**
     * Draws a [Card], making sure the proper amounts have been played.
     *
     * 108 of cards in total.
     */
    fun drawCard(): Card {
        if (playedCards.size == 108) { // All possible cards have been played, shuffle.
            val lastCard = playedCards.last()

            playedCards.apply {
                clear()
                add(lastCard)
            }
        }

        return when (Random.nextInt(15)) {
            // Wild Cards
            0 -> {
                // Lesser chance to get +4
                return if (Random.nextInt(3) == 0) {
                    val pre = PreCard(CardType.WILD_DEFAULT, CardColor.WILD, null)
                    if (canHaveCard(pre)) {
                        usedCards[pre] = (usedCards[pre] ?: 0) + 1
                        Card(CardColor.WILD, CardType.WILD_DEFAULT, null)
                    } else drawCard()
                } else {
                    val pre = PreCard(CardType.WILD_DEFAULT, CardColor.WILD, null)
                    if (canHaveCard(pre)) {
                        usedCards[pre] = (usedCards[pre] ?: 0) + 1
                        Card(CardColor.WILD, CardType.WILD_DEFAULT, null)
                    } else drawCard()
                }
            }

            // Skip Cards
            1 -> {
                val color = randomColor()
                val pre = PreCard(CardType.SKIP, color, null)
                if (canHaveCard(pre)) {
                    usedCards[pre] = (usedCards[pre] ?: 0) + 1
                    Card(color, CardType.SKIP, null)
                } else drawCard()
            }

            // Reverse Cards
            2 -> {
                val color = randomColor()
                val pre = PreCard(CardType.REVERSE, color, null)
                if (canHaveCard(pre)) {
                    usedCards[pre] = (usedCards[pre] ?: 0) + 1
                    Card(color, CardType.REVERSE, null)
                } else drawCard()
            }

            // Draw Two
            3 -> {
                val color = randomColor()
                val pre = PreCard(CardType.DRAW_TWO, color, null)
                if (canHaveCard(pre)) {
                    usedCards[pre] = (usedCards[pre] ?: 0) + 1
                    Card(color, CardType.DRAW_TWO, null)
                } else drawCard()
            }

            // Reverse
            4 -> {
                val color = randomColor()
                val pre = PreCard(CardType.REVERSE, color, null)
                if (canHaveCard(pre)) {
                    usedCards[pre] = (usedCards[pre] ?: 0) + 1
                    Card(color, CardType.REVERSE, null)
                } else drawCard()
            }

            // Default
            else -> {
                val num = Random.nextInt(10) // 0 -> 9

                val color = randomColor()
                val pre = PreCard(CardType.REGULAR, color, num)
                if (canHaveCard(pre)) {
                    usedCards[pre] = (usedCards[pre] ?: 0) + 1
                    Card(color, CardType.REGULAR, num)
                } else drawCard()
            }
        }
    }

    /**
     * Gets a random [CardColor], excluding [CardColor.WILD].
     */
    private fun randomColor(): CardColor {
        val rand = CardColor.values().random()

        // Re-pick if wild.
        return if (rand == CardColor.WILD)
            randomColor()
        else rand
    }

    /**
     * Checks if the user can have a [Card].
     *
     * Makes sure there's a proper amount of cards within the game.
     */
    private fun canHaveCard(preCard: PreCard): Boolean {
        return when (preCard.cardType) {
            CardType.WILD_DEFAULT -> (usedCards[preCard] ?: 0) < 4
            CardType.WILD_DRAW -> (usedCards[preCard] ?: 0) < 4
            CardType.SKIP -> (usedCards[preCard] ?: 0) < 4
            CardType.REVERSE -> (usedCards[preCard] ?: 0) < 2
            CardType.DRAW_TWO -> (usedCards[preCard] ?: 0) < 2
            CardType.REGULAR -> {
                if (preCard.num!! == 0) {
                    (usedCards[preCard] ?: 0) == 0
                } else (usedCards[preCard] ?: 0) < 2

            }
        }
    }

    /**
     * Checks if the user can play the current [card].
     *
     * If the [card] is wild, it can be played no matter what. If it's not, it must check if the
     */
    private fun canPlayCard(card: Card): Boolean {
        return when (card.type) {
            CardType.WILD_DEFAULT -> true
            CardType.WILD_DRAW -> true

            else -> {
                val otherCard = playedCards.last()

                // Checks if the color is the same, the number is the same if it is a number card, or if the type of a non-regular card is the same.
                card.color == otherCard.color
                        || (card.num != null && otherCard.num != null && otherCard.num == card.num)
                        || (card.type != CardType.REGULAR && card.type == otherCard.type)
            }
        }
    }

    /**
     * Places down and returns the initial [Card] of the game.
     */
    private fun getInitialCard(): Card {
        if (playedCards.size != 0) throw IllegalArgumentException("Game already started!")

        val card = drawCard()

        return when (card.type) {
            CardType.REGULAR -> {
                playedCards.add(card)
                card
            }
            else -> getInitialCard()
        }
    }

    /**
     * Gives the Chad and the player 7 cards, and returns [getInitialCard].
     */
    fun initGame(): Card {
        for (i in 0..6) {
            userCards.add(drawCard())
            botCards.add(drawCard())
        }

        return getInitialCard()
    }

    companion object {
        /**
         * Store of [UnoGame]s
         */
        val games = ConcurrentHashMap<IUser, UnoGame>()

        /**
         * Gets or creates an [UnoGame] from [games].
         *
         * Returns if it has just started in a [Pair].
         */
        fun getGame(iUser: IUser): Pair<Boolean, UnoGame> {
            if (!games.containsKey(iUser)) {
                games[iUser] = object : UnoGame(iUser) {}
                return Pair(true, games[iUser]!!)
            }

            return Pair(false, games[iUser]!!)
        }
    }
}