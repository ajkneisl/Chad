package dev.shog.chad.framework.obj

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate
import dev.shog.chad.framework.handle.PlayerHandler
import dev.shog.chad.framework.handle.database.DatabaseManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Information about a cached user
 *
 * @author sho, codebasepw
 */
class Player {
    /**
     * The user's ID
     */
    private val userId: Long

    /**
     * The player's full set of data
     */
    private var playerData = ConcurrentHashMap<DataType, Any>()

    /**
     * The types of data you can request from a player.
     *
     * 0 -> Long
     * 1 -> Integer
     * 2 -> String
     * 3 -> ArrayList<Any>
     */
    enum class DataType(val type: Int, val defaultValue: Any? = null) {
        // Other
        BALANCE(0),
        XP(0), RANK(1),

        // Profile
        PROFILE_UPVOTE(0),
        PROFILE_DOWNVOTE(0), PROFILE_DESCRIPTION(2), PROFILE_TITLE(2),

        // Data
        GUILD_DATA(2, "[]"),
        MARRY_DATA(2), VOTE_DATA(2), LAST_DAILY_REWARD(2)
    }

    /**
     * Default Constructor, sets it with default data.
     */
    constructor(user: Long) {
        this.userId = user
        playerData[DataType.BALANCE] = 0L
        playerData[DataType.RANK] = 0
        playerData[DataType.XP] = 0L
        playerData[DataType.MARRY_DATA] = "none&none"
        playerData[DataType.PROFILE_TITLE] = "none"

        playerData[DataType.PROFILE_DOWNVOTE] = 0L
        playerData[DataType.PROFILE_UPVOTE] = 0L

        playerData[DataType.VOTE_DATA] = ArrayList<Any>()
        playerData[DataType.GUILD_DATA] = "[]"
        playerData[DataType.LAST_DAILY_REWARD] = "none"
    }

    /**
     * Sets a player with existing values
     *
     * @param playerData The preset values
     */
    constructor(playerData: ConcurrentHashMap<DataType, Any>, user: Long) {
        this.playerData = playerData
        this.userId = user
    }

    /**
     * Sets a data type's value.
     *
     * @param dataType The data type to set
     * @param value The value to set it to
     */
    fun setObject(dataType: DataType, value: Any) {
        playerData[dataType] = value

        DatabaseManager.USER_DATA.setObjects(userId, AttributeUpdate(dataType.toString().toLowerCase()).put(value))
    }

    /**
     * Gets data from a data type.
     *
     * @param dataType The data type to retrieve
     * @return The retrieved data
     */
    fun getObject(dataType: DataType): Any? = playerData[dataType]
}
