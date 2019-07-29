package dev.shog.chad.framework.handle.uno.obj

import dev.shog.chad.core.getLogger
import dev.shog.chad.framework.handle.uno.handle.UnoStatistics
import sx.blah.discord.handle.obj.IUser
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

/**
 * The main Uno game. This holds all data, including the played [Card]s etc.
 *
 * @param iUser The user who created the game instance.
 */
class UnoGame(val iUser: IUser) {
    /**
     * The [iUser]'s player class.
     */
    val user = Player(this)

    /**
     * Chad's player class.
     */
    val chad = Player(this)

    /**
     * If the [player] is [chad], return [user]. Vice versa.
     */
    fun getOtherUser(player: Player): Player {
        return if (player == chad)
            user
        else chad
    }

    /**
     * A user within the Uno Game.
     */
    class Player internal constructor(private val uno: UnoGame) {
        /**
         * The cards
         */
        class CardCollection internal constructor() {
            /**
             * The size of [cards].
             */
            fun getSize() = cards.size

            /**
             * The player's cards.
             */
            val cards = ArrayList<Card>()

            /**
             * Add a card
             */
            fun add(card: Card) {
                cards.add(card)
            }

            /**
             * If the collection contains the inputted card, remove it.
             */
            fun remove(card: Card) {
                when (card.type) {
                    CardType.WILD_DEFAULT -> {
                        cards.forEach {
                            if (it.type == CardType.WILD_DEFAULT) {
                                cards.remove(it)
                                return
                            }
                        }
                    }

                    CardType.WILD_DRAW -> {
                        cards.forEach {
                            if (it.type == CardType.WILD_DRAW) {
                                cards.remove(it)
                                return
                            }
                        }
                    }

                    else -> cards.remove(card)
                }
            }

            /**
             * If the collection contains the inputted card.
             */
            fun contains(card: Card): Boolean {
                when (card.type) {
                    CardType.WILD_DEFAULT -> {
                        cards.forEach {
                            if (it.type == CardType.WILD_DEFAULT)
                                return true
                        }

                        return false
                    }

                    CardType.WILD_DRAW -> {
                        cards.forEach {
                            if (it.type == CardType.WILD_DRAW)
                                return true
                        }

                        return false
                    }

                    CardType.REGULAR -> {
                        cards.forEach {
                            if (it.color == card.color || it.num == card.num)
                                return true
                        }

                        return false
                    }

                    CardType.SKIP -> {
                        cards.forEach {
                            if (it.type == card.type || it.color == card.color)
                                return true
                        }

                        return false
                    }

                    CardType.REVERSE -> {
                        cards.forEach {
                            if (it.type == card.type || it.color == card.color)
                                return true
                        }

                        return false
                    }

                    CardType.DRAW_TWO -> {
                        cards.forEach {
                            if (it.type == card.type || it.color == card.color)
                                return true
                        }

                        return false
                    }
                }
            }
        }
        /**
         * The player's cards.
         */
        val cards = CardCollection()

        /**
         * Draws a card for the player [times] times.
         */
        fun draw(times: Int = 1): ArrayList<Card> {
            val drawn = ArrayList<Card>()

            repeat(times) {
                cards.add(uno.drawCard().also {
                    drawn.add(it)
                })
            }

            return drawn
        }

        /**
         * Play a card.
         *
         * @return First: is okay; Second: Turn Skipped
         */
        fun play(card: Card): Pair<Boolean, Boolean> {
            // If the user's card array contains the card they're attempting to play, and if they can play the card.
            if (!cards.contains(card.getAsClearWild()) || !uno.canPlayCard(card.getAsClearWild())) return Pair(first = false, second = false)

            // If the next user's turn has been skipped
            var turnSkipped = false

            when (card.type) {
                // Changes the color
                CardType.WILD_DEFAULT -> {}

                // Changes color and the other user and draws 4
                CardType.WILD_DRAW -> {
                    turnSkipped = true
                    uno.getOtherUser(this).draw(4)
                }

                // Skips the other user's turn
                CardType.SKIP -> {
                    turnSkipped = true
                }

                // Reverse does nothing
                CardType.REVERSE -> {}

                // Gets the other user and draws 2
                CardType.DRAW_TWO -> {
                    turnSkipped = true
                    uno.getOtherUser(this).draw(2)
                }

                CardType.REGULAR -> {}
            }

            // Removes the card from the list, and adds to the played cards.
            cards.remove(card)
            uno.playedCards.add(card)

            return Pair(true, turnSkipped)
        }
    }

    /**
     * The amount of times a [Card] has been used.
     */
    val usedCards = ConcurrentHashMap<Card, Int>()

    /**
     * The played [Card]s.
     */
    val playedCards = ArrayList<Card>()

    /**
     * Ends the game after a user runs out of cards.
     */
    fun endGame(userWon: Boolean) {
        games.remove(iUser)

        if (userWon) {
            UnoStatistics.gamesWon++
        } else UnoStatistics.botGamesWon++
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
                return if (Random.nextInt(2) == 0) {
                    val pre = Card(null, CardType.WILD_DRAW, null)
                    if (canHaveCard(pre)) {
                        usedCards[pre] = (usedCards[pre] ?: 0) + 1
                        Card(null, CardType.WILD_DRAW, null)
                    } else drawCard()
                } else {
                    val pre = Card(null, CardType.WILD_DEFAULT, null)
                    if (canHaveCard(pre)) {
                        usedCards[pre] = (usedCards[pre] ?: 0) + 1
                        Card(null, CardType.WILD_DEFAULT, null)
                    } else drawCard()
                }
            }

            // Skip Cards
            1 -> {
                val color = randomColor()
                val pre = Card(color, CardType.SKIP, null)
                if (canHaveCard(pre)) {
                    usedCards[pre] = (usedCards[pre] ?: 0) + 1
                    Card(color, CardType.SKIP, null)
                } else drawCard()
            }

            // Reverse Cards
            2 -> {
                val color = randomColor()
                val pre = Card(color,CardType.REVERSE, null)
                if (canHaveCard(pre)) {
                    usedCards[pre] = (usedCards[pre] ?: 0) + 1
                    Card(color, CardType.REVERSE, null)
                } else drawCard()
            }

            // Draw Two
            3 -> {
                val color = randomColor()
                val pre = Card(color, CardType.DRAW_TWO, null)
                if (canHaveCard(pre)) {
                    usedCards[pre] = (usedCards[pre] ?: 0) + 1
                    Card(color, CardType.DRAW_TWO, null)
                } else drawCard()
            }

            // Reverse
            4 -> {
                val color = randomColor()
                val pre = Card(color, CardType.REVERSE, null)
                if (canHaveCard(pre)) {
                    usedCards[pre] = (usedCards[pre] ?: 0) + 1
                    Card(color, CardType.REVERSE, null)
                } else drawCard()
            }

            // Default
            else -> {
                val num = Random.nextInt(10) // 0 -> 9

                val color = randomColor()
                val pre = Card(color, CardType.REGULAR, num)
                if (canHaveCard(pre)) {
                    usedCards[pre] = (usedCards[pre] ?: 0) + 1
                    Card(color, CardType.REGULAR, num)
                } else drawCard()
            }
        }
    }

    /**
     * Gets a random [CardColor].
     */
    private fun randomColor(): CardColor = CardColor.values().random()

    /**
     * Checks if the user can have a [Card].
     *
     * Makes sure there's a proper amount of cards within the game.
     */
    private fun canHaveCard(Card: Card): Boolean {
        return when (Card.type) {
            CardType.WILD_DEFAULT -> (usedCards[Card] ?: 0) < 4
            CardType.WILD_DRAW -> (usedCards[Card] ?: 0) < 4
            CardType.SKIP -> (usedCards[Card] ?: 0) < 4
            CardType.REVERSE -> (usedCards[Card] ?: 0) < 2
            CardType.DRAW_TWO -> (usedCards[Card] ?: 0) < 2
            CardType.REGULAR -> {
                if (Card.num!! == 0) {
                    (usedCards[Card] ?: 0) == 0
                } else (usedCards[Card] ?: 0) < 2

            }
        }
    }

    /**
     * Checks if the user can play the current [c].
     *
     * If the [c] is wild, it can be played no matter what. If it's not, it must check if the
     */
    private fun canPlayCard(c: Card): Boolean {
        val card = c.getAsClearWild()

        return when (card.type) {
            CardType.WILD_DEFAULT -> true
            CardType.WILD_DRAW -> true

            else -> {
                val otherCard = playedCards.last()

                // Checks if the color is the same, the number is the same if it is a number card, or if the type of a non-regular card is the same.
                (card.color == otherCard.color
                        || (card.num != null && otherCard.num != null && otherCard.num == card.num)
                        || (card.type != CardType.REGULAR && card.type == otherCard.type))
            }
        }
    }

    /**
     * Places down and returns the initial [Card] of the game. Cannot be anything but a [CardType.REGULAR]
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
        user.draw(7)
        chad.draw(7)

        return getInitialCard()
    }

    /**
     * What time the game started at.
     */
    val startedAt = System.currentTimeMillis()

    /**
     * If the user called Uno, meaning that they had 1 card left.
     */
    var userCalledUno = false

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
                games[iUser] = UnoGame(iUser)
                return Pair(true, games[iUser]!!)
            }

            return Pair(false, games[iUser]!!)
        }
    }
}