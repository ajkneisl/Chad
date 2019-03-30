package org.woahoverflow.chad.framework.handle

import org.bson.Document
import org.woahoverflow.chad.framework.handle.database.DatabaseManager
import org.woahoverflow.chad.framework.obj.Player
import org.woahoverflow.chad.framework.obj.Player.DataType
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages Player instances
 *
 * @author sho, codebasepw
 */
object PlayerHandler {
    /**
     * The local cached players
     */
    @JvmStatic
    private val players = ConcurrentHashMap<Long, Player>()

    /**
     * If a player exists within the hash-map
     *
     * @param player The player to check with
     * @return If it contains it
     */
    @JvmStatic
    fun playerExists(player: Long): Boolean {
        return players.containsKey(player)
    }

    /**
     * Refreshes a player into the hashmap
     *
     * @param user The user to refresh
     */
    @JvmStatic
    fun refreshPlayer(user: Long) {
        if (players.keys.contains(user))
            players[user] = getPlayer(user)
    }

    /**
     * Removes a player dataset from the database
     *
     * @param user The user to remove
     */
    @JvmStatic
    fun removePlayer(user: Long) {
        // Removes it from the hashmap
        players.remove(user)

        // Removes it from the database
        val col = DatabaseManager.USER_DATA.collection

        val get = col.find(Document("id", user)).first() ?: return

        col.deleteOne(get)
    }

    /**
     * Creates a player
     *
     * @param user The user's ID to register
     */
    @JvmStatic
    private fun createPlayer(user: Long): Player {
        val playerDocument = Document()

        // The user's ID
        playerDocument["id"] = user

        // The user's balance
        playerDocument["balance"] = 0L

        // The user's XP
        playerDocument["xp"] = 0L

        // The user's rank
        playerDocument["rank"] = 0

        // Other default user data
        playerDocument["marry_data"] = "none&none"
        playerDocument["profile_description"] = "No description!"
        playerDocument["profile_title"] = "none"
        playerDocument["profile_downvote"] = 0L
        playerDocument["profile_upvote"] = 0L

        playerDocument["guild_data"] = ArrayList<Any>()
        playerDocument["vote_data"] = ArrayList<Any>()

        playerDocument["last_daily_reward"] = "none"

        // Insert the new player=
        DatabaseManager.USER_DATA.collection.insertOne(playerDocument)

        // The player
        val player = parsePlayer(playerDocument, user)

        // Add it into the hash map
        players[user] = player

        // Return the new player
        return player
    }

    /**
     * Gets a player's data from the database
     *
     * @param playerDataDocument The player to get
     * @return The player retrieved
     */
    @JvmStatic
    private fun parsePlayer(playerDataDocument: Document, user: Long): Player {
        val playerData = ConcurrentHashMap<DataType, Any>()

        // Sets the data
        for (type in DataType.values()) {
            if (playerDataDocument[type.toString().toLowerCase()] == null) {
                when (type.type) {
                    0 -> playerData[type] = 0L
                    1 -> playerData[type] = 0
                    2 -> playerData[type] = ""
                    3 -> playerData[type] = ArrayList<Any>()
                }
            } else {
                playerData[type] = playerDataDocument[type.toString().toLowerCase()]!!
            }
        }

        return Player(playerData, user)
    }

    /**
     * Gets a player instance
     *
     * @return The user's Player instance
     */
    @JvmStatic
    fun getPlayer(user: Long): Player {
        // If the user's in in the hash map, return it
        if (players.containsKey(user)) {
            return players[user]!!
        }

        if (userDataExists(user)) {
            val get = DatabaseManager.USER_DATA.collection.find(Document("id", user)).first()!!

            val player = parsePlayer(get, user)

            players[user] = player

            return player
        }

        val player = createPlayer(user)

        players[user] = player

        // If it's not in there, create one
        return player
    }

    /**
     * Checks if a user's data already exists within the database
     *
     * @param user The user to check
     * @return If it exists
     */
    @JvmStatic
    private fun userDataExists(user: Long): Boolean {
        val get = DatabaseManager.USER_DATA.collection.find(Document("id", user)).first()
        return get != null
    }
}
