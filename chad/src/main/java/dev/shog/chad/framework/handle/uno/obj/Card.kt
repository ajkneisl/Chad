package dev.shog.chad.framework.handle.uno.obj

/**
 * Holds data about a Card for an [UnoGame].
 *
 * If the card is a wild card, [num] will be set to null.
 *
 * @param color The color of the card. This partly determines what card can be played on top of.
 * @param type The type of the card.
 * @param num The number of the card. This also party determines what the card can be played on top of.
 */
class Card (val color: CardColor, val type: CardType, val num: Int?) {
    /**
     * Formats the card into a prettier version.
     */
    override fun toString(): String {
        // The type of the string. If it's a regular card, return the number.
        val strType: String = when (type) {
            CardType.WILD_DEFAULT -> "Wild"
            CardType.WILD_DRAW -> "Wild +4"
            CardType.SKIP -> "Skip"
            CardType.REVERSE -> "Reverse"
            CardType.DRAW_TWO -> "+2"
            CardType.REGULAR -> (num ?: -1).toString()
        }

        val color = color.name.toLowerCase().capitalize()

        // If it's wild, it doesn't require the color.
        return if (this.color == CardColor.WILD)
            strType
        else "$color $strType"
    }
}