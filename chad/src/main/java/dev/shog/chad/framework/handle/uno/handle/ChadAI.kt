package dev.shog.chad.framework.handle.uno.handle

import dev.shog.chad.core.getLogger
import dev.shog.chad.framework.handle.uno.obj.Card
import dev.shog.chad.framework.handle.uno.obj.CardColor
import dev.shog.chad.framework.handle.uno.obj.CardType
import dev.shog.chad.framework.handle.uno.obj.UnoGame

/**
 * Creates plays in a [UnoGame] for the bot.
 *
 * This does not actually play the card into the game, but finds the [Card] that the bot should play.
 */
class ChadAI(private val unoGame: UnoGame) {
    /**
     * The last played card
     */
    private lateinit var recentCard: Card

    /**
     * All cards
     */
    private lateinit var cards: ArrayList<Card>

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

    /**
     * Calculates the different variables in [ChadAI].
     */
    private fun calculate() {
        wild.clear()
        wildDraw.clear()
        plusTwo.clear()
        reverse.clear()
        skip.clear()
        reg.clear()
        cards = unoGame.botCards
        recentCard = unoGame.playedCards.last()

        cards.forEach { card ->
            when (card.type) {
                CardType.WILD_DEFAULT -> wild.add(card)
                CardType.WILD_DRAW -> wildDraw.add(card)
                CardType.SKIP -> skip.add(card)
                CardType.REVERSE -> reverse.add(card)
                CardType.DRAW_TWO -> plusTwo.add(card)
                CardType.REGULAR -> reg.add(card)
            }
        }
    }

    init { calculate() }

    /**
     * Gets a [Card] that's able to be played.
     */
    fun play(): Card {
        // If they've got less cards, attempt to get a +2 card.
        if (unoGame.userCards.size < cards.size && plusTwo.isNotEmpty()) {
            val card = attemptPlusTwo()

            if (card != null)
                return card
        }

        // If they've got less cards, attempt to get a +2 card.
        if (unoGame.userCards.size < cards.size && plusTwo.isNotEmpty()) {
            val card = attemptPlusTwo()

            if (card != null)
                return card
        }

        // If we've got more cards, attempt to skip them to be able to use more
        if (unoGame.userCards.size < cards.size && skip.isNotEmpty()) {
            val card = attemptSkip()

            if (card != null)
                return card
        }

        var card = attemptReverse()

        if (card != null)
            return card

        card = attemptRegular()

        if (card != null)
            return card

        card = attemptWild()

        if (card != null)
            return card

        println("Bot needed to draw for card. Needs to find a playable card ontop of $recentCard")
        unoGame.botCards.forEach { crd ->
            println(crd)
        }

        // Keep drawing until it finds the right card
        val drawn = unoGame.drawCard()

        unoGame.botCards.add(drawn)
        drawAttempts++

        getLogger().warn("The bot has drawn $drawAttempts times!")

        calculate()

        return play()
    }

    /**
     * The amount of times the bot had to draw until it found a card.
     */
    private var drawAttempts = 0

    /**
     * Attempts to play a wild [Card]. This is done when nothing else can played.
     */
    private fun attemptWild(): Card? {
        val draw = wildDraw.isNotEmpty()
        val reg = wild.isNotEmpty()

        if (draw || reg) {
            val largest = getLargestCard()

            when {
                // Draw isn't empty and regulars are: Play draw
                draw && !reg -> {
                    unoGame.botCards.remove(Card(CardColor.WILD, CardType.WILD_DRAW, null))

                    val card = Card(largest, CardType.WILD_DRAW, null)
                    unoGame.botCards.add(card)

                    return card
                }

                // Draw is empty and regulars aren't: Play regular
                reg && !draw -> {
                    unoGame.botCards.remove(Card(CardColor.WILD, CardType.WILD_DEFAULT, null))

                    val card = Card(largest, CardType.WILD_DEFAULT, null)
                    unoGame.botCards.add(card)

                    return card
                }

                draw && reg -> {
                    return if (unoGame.userCards.size > unoGame.botCards.size) { // If the user has more cards, play a regular one as we're ahead.
                        unoGame.botCards.remove(Card(CardColor.WILD, CardType.WILD_DEFAULT, null))

                        val card = Card(largest, CardType.WILD_DEFAULT, null)
                        unoGame.botCards.add(card)

                        card
                    } else { // If we're the same, or they're ahead, use +4
                        unoGame.botCards.remove(Card(CardColor.WILD, CardType.WILD_DRAW, null))

                        val card = Card(largest, CardType.WILD_DRAW, null)
                        unoGame.botCards.add(card)

                        card
                    }
                }
            }
        }

        return null
    }

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
            println("Recent Card Color: ${recentCard.color}\nCard Color: ${crd.color}\n\nRecent Card Number: ${recentCard.num}\nCard Number: ${crd.num}")

            if (recentCard.color == crd.color || (recentCard.num != null && recentCard.num == crd.num))
                return crd
        }

        return null
    }

    /**
     * Gets the card color that has the most amount of [Card]s in Chad's deck.
     */
    private fun getLargestCard(): CardColor {
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

        var biggestCard = CardColor.WILD
        var biggestVal = 0

        arrayListOf(Pair(CardColor.YELLOW, yellow), Pair(CardColor.GREEN, green), Pair(CardColor.BLUE, blue), Pair(CardColor.RED, red)).forEach {
            if (it.second > biggestVal) {
                biggestCard = it.first
                biggestVal = it.second
            }
        }

        return biggestCard
    }
}