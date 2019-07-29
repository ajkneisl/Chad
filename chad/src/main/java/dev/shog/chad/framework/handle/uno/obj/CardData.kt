package dev.shog.chad.framework.handle.uno.obj

/**
 * The type of [Card].
 *
 * Data has been retrieved from https://en.wikipedia.org/wiki/Uno_(card_game)
 */
enum class CardType {
    /**
     *  Player declares the next color to be matched (may be used on any turn even if the player has matching color)
     */
    WILD_DEFAULT,

    /**
     * Player declares the next color to be matched; next player in sequence draws four cards and misses a turn. May be legally played only if the player has no cards of the current color (see Penalties).
     */
    WILD_DRAW,

    /**
     * Next player in sequence draws two cards and misses a turn
     */
    SKIP,

    /**
     * Order of play switches directions (clockwise to counterclockwise, or vice versa)
     */
    REVERSE,

    /**
     * Next player in sequence draws two cards and misses a turn
     */
    DRAW_TWO,

    /**
     * A regular colorful card.
     */
    REGULAR
}

/**
 * The color of a [Card].
 */
enum class CardColor {
    YELLOW, GREEN, BLUE, RED
}