package dev.shog.chad.framework.handle.uno.obj

import dev.shog.chad.core.getLogger

/**
 * Creates plays in a [UnoGame] for the bot.
 *
 * This does not actually play the card into the game, but finds the [Card] that the bot should play.
 */
class ChadAI(private val unoGame: UnoGame) {
    /**
     * A response from [getAmounts].
     */
    private data class Response(val yellow: Int, val red: Int, val green: Int, val blue: Int)

    /**
     * The last played card
     */
    private val recentCard = unoGame.playedCards.last()

    /**
     * All cards
     */
    private val cards = unoGame.botCards

    /**
     * Default wild cards.
     */
    private val wild = ArrayList<Card>()

    /**
     * +4 wild cards.
     */
    private val wildDraw = ArrayList<Card>()

    /**
     * The +2 cards.
     */
    private val plusTwo = ArrayList<Card>()

    /**
     * The skip cards
     */
    private val reverse = ArrayList<Card>()

    /**
     * The skip cards.
     */
    private val skip = ArrayList<Card>()

    /**
     * The regular numeric cards.
     */
    private val reg = ArrayList<Card>()

    init {
        // Sorts cards into respected areas
        cards.forEach { crd ->
            when (crd.type) {
                CardType.WILD_DEFAULT -> wild.add(crd)
                CardType.WILD_DRAW -> wildDraw.add(crd)
                CardType.SKIP -> skip.add(crd)
                CardType.REVERSE -> reverse.add(crd)
                CardType.DRAW_TWO -> plusTwo.add(crd)
                CardType.REGULAR -> reg.add(crd)
            }
        }
    }

    fun play(): Card {
        when {
            // If they've got less cards, attempt to get a +2 card.
            unoGame.userCards.size < cards.size && plusTwo.isNotEmpty() -> {
                val card = attemptPlusTwo()

                if (card != null)
                    return card
            }

            // If we've got more cards, attempt to skip them to be able to use more
            unoGame.userCards.size < cards.size && skip.isNotEmpty() -> {
                val card = attemptSkip()

                if (card != null)
                    return card
            }

            else -> {
                var card = attemptReverse()

                if (card != null)
                    return card

                card = attemptRegular()

                if (card != null)
                    return card
            }
        }

        if (wild.isNotEmpty()) {
            val percentageResponse = getAmounts()
            var biggest = Pair(0, CardColor.WILD)

            arrayListOf(
                    Pair(percentageResponse.blue, CardColor.BLUE),
                    Pair(percentageResponse.red, CardColor.RED),
                    Pair(percentageResponse.yellow, CardColor.YELLOW),
                    Pair(percentageResponse.green, CardColor.GREEN)
            ).forEach { db ->
                if (db.first > biggest.first) biggest = db
            }

            when {
                // If there's no wild draw cards available.
                wildDraw.size == 0 && wild.size > 0 -> {
                    val card = Card(biggest.second, CardType.WILD_DEFAULT, null)
                    val getCard = wild[0]

                    unoGame.botCards.remove(getCard)
                    unoGame.botCards.add(card)

                    return card
                }

                // If there's no wild cards available.
                wild.size == 0 && wildDraw.size > 0 -> {
                    val card = Card(biggest.second, CardType.WILD_DRAW, null)
                    val getCard = wildDraw[0]

                    unoGame.botCards.remove(getCard)
                    unoGame.botCards.add(card)

                    return card
                }

                unoGame.userCards.size < cards.size && wildDraw.size > 0 -> {
                    val card = Card(biggest.second, CardType.WILD_DRAW, null)
                    val getCard = wildDraw[0]

                    unoGame.botCards.remove(getCard)
                    unoGame.botCards.add(card)

                    return card
                }

                // If they've got the choice, and the enemies have more cards, just use a regular one.
                wild.size > 0 -> {
                    val card = Card(biggest.second, CardType.WILD_DEFAULT, null)
                    val getCard = wild[0]

                    unoGame.botCards.remove(getCard)
                    unoGame.botCards.add(card)

                    return card
                }
            }
        }

        println("Bot needed to draw for card. Needs to find a playable card ontop of $recentCard")
        unoGame.botCards.forEach { crd ->
            println(crd)
        }

        // Keep drawing until it finds the right card
        val drawn = unoGame.drawCard()

        unoGame.botCards.add(drawn)
        drawAttempts++

        getLogger().warn("The bot has drawn $drawAttempts times!")

        return ChadAI(unoGame).play()
    }

    /**
     * The amount of times the bot had to draw until it found a card.
     */
    private var drawAttempts = 0

    /**
     * Attempts to play a +2 [Card]. Returns null if not.
     */
    private fun attemptPlusTwo(): Card? {
        plusTwo.forEach { crd ->
            if (recentCard.color == crd.color)
                return crd
        }

        return null
    }

    /**
     * Attempts to play a skip [Card]. Returns null if not.
     */
    private fun attemptSkip(): Card? {
        skip.forEach { crd ->
            if (recentCard.color == crd.color)
                return crd
        }

        return null
    }


    /**
     * Attempts to play a reverse [Card]. Returns null if not.
     *
     * This is basically useless, since it gets to the user either way. Used as a regular card.
     */
    private fun attemptReverse(): Card? {
        reverse.forEach { crd ->
            if (recentCard.color == crd.color)
                return crd
        }

        return null
    }

    /**
     * Attempts to play a regular [Card]. Returns null if not available.
     *
     * Checks color as well as the number
     */
    private fun attemptRegular(): Card? {
        reg.forEach { crd ->
            if (recentCard.color == crd.color || (recentCard.num != null && recentCard.num == crd.num))
                return crd
        }

        return null
    }

    /**
     * Gets the amount of cards that are a specific color, and returns a [Response].
     */
    private fun getAmounts(): Response {
        val am = cards.size

        var yellow = 0
        var green = 0
        var blue = 0
        var red = 0

        cards.forEach { card ->
            when (card.color) {
                CardColor.YELLOW -> yellow++
                CardColor.GREEN -> green++
                CardColor.BLUE -> blue++
                CardColor.RED -> red++
                else -> {}
            }
        }

        return Response(yellow, red, green, blue)
    }
}