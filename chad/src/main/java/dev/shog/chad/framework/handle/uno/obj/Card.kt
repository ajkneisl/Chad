package dev.shog.chad.framework.handle.uno.obj

/**
 * Holds data about a Card for an [UnoGame].
 *
 * If the card is a wild card, [num] will be set to null.
 *
 * @param color The color of the card. This partly determines what card can be played on top of. If this is null, it's a wild card.
 * @param type The type of the card.
 * @param num The number of the card. This also party determines what the card can be played on top of. If it's null, it's a wild card.
 */
class Card (val color: CardColor?, val type: CardType, val num: Int?) {
    /**
     * Overridden equal.
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Card)
            return false

        return other.color == color && other.type == type && other.num == num
    }

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

        val color = color?.name?.toLowerCase()?.capitalize()

        // If it's wild, it doesn't require the color.
        return if (this.color == null)
            strType
        else "$color $strType"
    }

    /**
     * If the card is wild, create a card that has a null value of [color] and [num]. If the card isn't a wild card, return this.
     */
    fun getAsClearWild(): Card {
        return if (type == CardType.WILD_DEFAULT || type == CardType.WILD_DRAW) {
            Card(null, type, null)
        } else this
    }

    /**
     * The hashcode
     */
    override fun hashCode(): Int {
        var result = color?.hashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + (num ?: 0)
        return result
    }
}