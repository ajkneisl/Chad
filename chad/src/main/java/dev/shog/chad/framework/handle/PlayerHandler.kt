package dev.shog.chad.framework.handle

import com.amazonaws.services.dynamodbv2.datamodeling.*
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.PrimaryKey
import dev.shog.chad.framework.handle.database.DatabaseManager
import dev.shog.chad.framework.handle.dynamo.DynamoDB
import dev.shog.chad.framework.obj.Player
import dev.shog.chad.framework.obj.Player.DataType
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages Player instances
 *
 * @author sho
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
    fun playerExists(player: Long): Boolean = players.containsKey(player)

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
        DatabaseManager.USER_DATA.table.deleteItem("id", user)
    }

    /**
     * Creates a player
     *
     * @param user The user's ID to register
     */
    @JvmStatic
    private fun createPlayer(user: Long): Player {
        val playerDocument = Item()

        // The user's ID
        playerDocument.with("id", user)

        // The user's balance
        playerDocument.with("balance", 0L)

        // The user's XP
        playerDocument.with("xp", 0L)

        // The user's rank
        playerDocument.with("rank", 0)

        // Other default user data
        playerDocument.with("marry_data", "none&none")
        playerDocument.with("profile_description", "No description!")
        playerDocument.with("profile_title", "none")
        playerDocument.with("profile_downvote", 0L)
        playerDocument.with("profile_upvote", 0L)

        playerDocument.with("guild_data", ArrayList<Any>())
        playerDocument.with("vote_data", ArrayList<Any>())

        playerDocument.with("last_daily_reward", "none")

        // Insert the new player
        DatabaseManager.USER_DATA.table.putItem(playerDocument)

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
    private fun parsePlayer(playerDataDocument: Item, user: Long): Player {
        val playerData = ConcurrentHashMap<DataType, Any>()

        // Sets the data
        for (type in DataType.values()) {
            if (playerDataDocument.get(type.toString()) == null) {
                if (type.defaultValue != null) {
                    playerData[type] = type.defaultValue
                } else {
                    when (type.type) {
                        0 -> playerData[type] = 0L
                        1 -> playerData[type] = 0
                        2 -> playerData[type] = ""
                        3 -> playerData[type] = ArrayList<Any>()
                    }
                }
            } else {
                playerData[type] = playerDataDocument.get(type.toString())!!
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
        if (players.containsKey(user)) return players[user]!!

        val get = DatabaseManager.USER_DATA.getObject(user)
        if (get != null) {
            val player = parsePlayer(get, user)

            players[user] = player

            return player
        }

        val player = createPlayer(user)

        players[user] = player

        // If it's not in there, create one
        return player
    }
}
