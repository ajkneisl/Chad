package org.woahoverflow.chad.framework.handle

import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.framework.obj.Player
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.RequestBuffer
import java.util.concurrent.ConcurrentHashMap

object LeaderboardHandler {
    /**
     * A set of the user and their balance
     */
    data class MoneyUserSet(val user: IUser, val bal: Long)

    /**
     * The user's XP set
     */
    data class XPUserSet(val user: IUser, val xp: Long, val rank: Int)

    /**
     * The amount of users it refreshed.
     */
    data class TimeResult(val am: Int, val time: Long)

    /**
     * The type of leaderboard (from the instances)
     */
    enum class LeaderboardType {
        MONEY, XP, WORLD
    }

    /**
     * A leaderboard
     *
     * This was intended to have multiple instances.
     */
    class LeaderBoard<E>(
            /**
             * The amount of people the leaderboard should have
             */
            val amount: Int,

            /**
             * The minimum amount for the leaderboard
             */
            val minimum: Long
    ) {
        /**
         * The stored leaderboard
         */
        private val leaderBoard = ConcurrentHashMap<Int, E>()

        /**
         * Gets the leaderboard
         */
        fun getLeaderBoard(): ConcurrentHashMap<Int, E> = leaderBoard

        /**
         * Sets the position on the leaderboard to a user.
         */
        fun setPos(loc: Int, e: E) {
            if (0 > loc) return

            leaderBoard[loc] = e
        }

        /**
         * Gets data about a position on the leaderboard.
         *
         * Will return null if the position isn't filled
         */
        fun getPos(loc: Int): E? = leaderBoard[loc]

        /**
         * Clears the leaderboard
         */
        fun reset() {
            leaderBoard.clear()
        }
    }

    /**
     * The leaderboards for Chad
     */
    val moneyLeaderBoard = LeaderBoard<MoneyUserSet>(10, 2000)
    val xpLeaderBoard = LeaderBoard<XPUserSet>(10, 20)

    /**
     * Refreshes the leaderboard
     */
    fun refreshLeaderboard(type: LeaderboardType): TimeResult {
        val start = System.currentTimeMillis()
        ChadInstance.getLogger().debug("Refreshing $type leaderboard...")

        when (type) {
            LeaderboardHandler.LeaderboardType.MONEY -> {
                val guilds = RequestBuffer.request<List<IGuild>> {
                    ChadInstance.cli.guilds
                }.get()

                // Gets all users and checks for duplicates
                val users = RequestBuffer.request<List<IUser>> {
                    val list = ArrayList<IUser>()
                    for (guild in guilds) {
                        for (user1 in guild.users) {
                            if (!checkDuplicate(list, user1)) list.add(user1)
                        }
                    }

                    list
                }.get()

                ChadInstance.getLogger().debug("Refreshing ${users.size} users...")

                moneyLeaderBoard.reset()
                val amount = moneyLeaderBoard.amount

                for (user in users) {
                    val player = PlayerHandler.getPlayer(user.longID)
                    val balance = player.getObject(Player.DataType.BALANCE) as Long

                    if (balance < moneyLeaderBoard.minimum) continue

                    for (i in 1..amount) {
                        val pos = moneyLeaderBoard.getPos(i)

                        if (pos == null) {
                            moneyLeaderBoard.setPos(i, MoneyUserSet(user, balance))
                            break
                        }

                        if (balance > pos.bal) {
                            moneyLeaderBoard.setPos(i, MoneyUserSet(user, balance))
                            checkUser(pos.bal, pos.user)
                            break
                        }
                    }
                }

                ChadInstance.getLogger().debug("Completed money leaderboard refresh! Took ${System.currentTimeMillis()-start}ms")
                return TimeResult(users.size, System.currentTimeMillis()-start)
            }

            LeaderboardHandler.LeaderboardType.XP -> TODO()
            LeaderboardHandler.LeaderboardType.WORLD -> TODO()
        }
    }

    /**
     * Rechecks a user's position on the money leaderboard
     */
    private fun checkUser(balance: Long, user: IUser) {
        val amount = moneyLeaderBoard.amount

        if (balance < moneyLeaderBoard.minimum) return

        for (i in 1..amount) {
            val pos = moneyLeaderBoard.getPos(i)

            if (pos == null) {
                moneyLeaderBoard.setPos(i, MoneyUserSet(user, balance))
                break
            }

            if (balance > pos.bal) {
                moneyLeaderBoard.setPos(i, MoneyUserSet(user, balance))
                checkUser(pos.bal, pos.user)
                break
            }
        }
    }

    /**
     * Makes sure there's not a duplicate user in the leaderboard
     */
    private fun checkDuplicate(lis: List<IUser>, corUser: IUser): Boolean {
        for (user in lis) {
            if (user.longID == corUser.longID) return true
        }

        return false
    }
}