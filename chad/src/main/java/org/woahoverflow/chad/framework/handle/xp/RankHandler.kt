package org.woahoverflow.chad.framework.handle.xp

import org.woahoverflow.chad.framework.obj.Player
import java.util.concurrent.ConcurrentHashMap

/**
 * Handles ranks
 *
 * @author sho
 */
object RankHandler {
    /**
     * The different ranks in Chad, with XP being the amount of XP to receive that rank
     */
    enum class Rank(val xp: Long, val id: Int) {
        NONE(0, 0), ONE(50, 1), TWO(400, 2), THREE(1000, 3), FOUR(2000, 4), FIVE(3000, 5), SIX(4500, 6), SEVEN(6000, 7), EIGHT(8000, 8), NINE(10000, 9), TEN(15000, 10), ELEVEN(20000, 11), TWELVE(30000, 12), THIRTEEN(45000, 13), FOURTEEN(70000, 14), FIFTEEN(100000, 15)
    }

    /**
     * The different ranks with their IDS
     */
    private val ranks = object : ConcurrentHashMap<Int, Rank>() {
        init {
            for (rank in Rank.values()) {
                put(rank.id, rank)
            }
        }
    }

    /**
     * Gets a rank from the hashmap through an ID
     */
    private fun getRankByID(id: Int): Rank? = ranks[id]

    /**
     * If the user can rank up
     */
    fun rankUp(player: Player): Boolean {
        val rank = getUserRank(player)
        val updated = getUserRank(player, refresh = true)

        return if (updated != rank) {
            player.setObject(Player.DataType.RANK, updated.id)
            true
        } else false
    }

    /**
     * Calculates a user's rank through their XP levels
     */
    private fun calculateUserRank(xp: Long): Rank {
        var userRank: Rank = RankHandler.Rank.NONE

        for (rank in Rank.values()) if (xp >= rank.xp) userRank = rank

        return userRank
    }

    /**
     * The amount of XP til the user can rank up
     */
    fun toNextRank(player: Player): Long {
        val xp = XPHandler.getUserXP(player)
        val aboveRank = getUpperRank(getUserRank(player, refresh = true)) ?: return 0

        return aboveRank.xp - xp
    }

    /**
     * The amount of XP the user is above their previous rank
     */
    fun aboveRank(player: Player): Long {
        val xp = XPHandler.getUserXP(player)
        val lowerRank = getLowerRank(getUserRank(player, refresh = true)) ?: return 0

        return xp - lowerRank.xp
    }

    /**
     * Gets the rank above the inputted rank
     */
    private fun getUpperRank(rank: Rank): Rank? = getRankByID(rank.id + 1)

    /**
     * Gets the rank below the inputted rank
     */
    fun getLowerRank(rank: Rank): Rank? = getRankByID(rank.id - 1)

    /**
     * Gets a user's rank
     */
    fun getUserRank(player: Player, refresh: Boolean = false): Rank {
        return when (refresh) {
            true -> calculateUserRank(XPHandler.getUserXP(player))
            false -> getRankByID(player.getObject(Player.DataType.RANK).toString().toInt())!!
        }
    }
}