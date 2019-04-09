package org.woahoverflow.chad.framework.obj

import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.database.DatabaseManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Information about a cached guild
 *
 * @author sho
 */
class Guild {
    /**
     * The guild's ID
     */
    /**
     * Gets the guild's ID
     *
     * @return The guild's ID
     */
    val guildID: Long

    /**
     * The guild's full set of data
     */
    private var guildData = ConcurrentHashMap<DataType, Any>()

    /**
     * The guild's full set of permission data
     */
    private val permissionData = ConcurrentHashMap<Long, ArrayList<String>>()

    /**
     * The amount of messages sent within the guild
     */
    private var messagesSent: Long = 0

    /**
     * The amount of commands sent within the guild
     */
    private var commandsSent: Long = 0

    /**
     * The types of data you can request from a guild.
     */
    enum class DataType {
        // Main
        PREFIX,

        // Logging
        LOGGING,
        LOGGING_CHANNEL,

        // Messages
        JOIN_MESSAGE_ON,
        LEAVE_MESSAGE_ON, JOIN_MESSAGE_CHANNEL, LEAVE_MESSAGE_CHANNEL, BAN_MESSAGE_ON, KICK_MESSAGE_ON,
        BAN_MESSAGE, KICK_MESSAGE, JOIN_MESSAGE, LEAVE_MESSAGE,

        // Join Role
        ROLE_ON_JOIN,
        JOIN_ROLE,

        // Swearing
        SWEAR_FILTER,
        SWEAR_FILTER_MESSAGE,

        // Statistics
        MESSAGES_SENT,
        COMMANDS_SENT,

        // Other
        DISABLED_CATEGORIES
    }

    /**
     * Default Constructor, sets it with default data.
     */
    constructor(guild: Long) {
        this.guildID = guild

        // Main
        guildData[DataType.PREFIX] = "c!"

        // Logging
        guildData[DataType.LOGGING] = false
        guildData[DataType.LOGGING_CHANNEL] = "none"

        // Statistics
        guildData[DataType.MESSAGES_SENT] = 0L
        guildData[DataType.COMMANDS_SENT] = 0L

        messagesSent = 0L
        commandsSent = 0L

        // Messages
        guildData[DataType.JOIN_MESSAGE] = "`&user&` has joined the server!"
        guildData[DataType.JOIN_MESSAGE_ON] = false
        guildData[DataType.JOIN_MESSAGE_CHANNEL] = "none"

        guildData[DataType.LEAVE_MESSAGE] = "`&user` has left the server!"
        guildData[DataType.LEAVE_MESSAGE_ON] = false
        guildData[DataType.LEAVE_MESSAGE_CHANNEL] = "none"

        guildData[DataType.BAN_MESSAGE] = "`&user& has been banned for `&reason&`!"
        guildData[DataType.KICK_MESSAGE] = "`&user& has been kicked for `&reason&`!"

        guildData[DataType.BAN_MESSAGE_ON] = true
        guildData[DataType.KICK_MESSAGE_ON] = true

        // Join Role
        guildData[DataType.JOIN_ROLE] = "none"
        guildData[DataType.ROLE_ON_JOIN] = false

        // Swearing
        guildData[DataType.SWEAR_FILTER] = false
        guildData[DataType.SWEAR_FILTER_MESSAGE] = "Stop swearing, `&user&`!"

        // Other
        guildData[DataType.DISABLED_CATEGORIES] = ArrayList<String>()
    }

    /**
     * Sets a guild with existing values
     *
     * @param guildData The preset values
     */
    constructor(guildData: ConcurrentHashMap<DataType, Any>, guild: Long) {
        this.guildData = guildData
        this.guildID = guild

        messagesSent = guildData[DataType.MESSAGES_SENT] as Long
        commandsSent = guildData[DataType.COMMANDS_SENT] as Long
    }

    /**
     * Sets a data type's value.
     *
     * @param dataType The data type to set
     * @param value The value to set it to
     */
    fun setObject(dataType: DataType, value: Any) {
        guildData[dataType] = value

        DatabaseManager.GUILD_DATA.setObject(guildID, dataType.toString().toLowerCase(), value)
    }

    /**
     * Gets data from a data type.
     *
     * @param dataType The data type to retrieve
     * @return The retrieved data
     */
    fun getObject(dataType: DataType): Any {
        return guildData[dataType]!!
    }

    /**
     * Gets data from a string, directly from the database.
     *
     * @param dataType The data's key
     * @return The retrieved data
     */
    fun getObject(dataType: String): Any {
        return DatabaseManager.USER_DATA.getObject(guildID, dataType)
    }

    /**
     * Gets the permissions for a role
     *
     * @param role The role to get permissions for
     * @return The role's permissions
     */
    fun getRolePermissions(role: Long): ArrayList<String> {
        // If the data's in the permission hash-map, return that
        if (permissionData.containsKey(role))
            return permissionData[role]!!

        // Get the permissions from the database
        val permissions = DatabaseManager.GUILD_DATA.getObject(guildID, java.lang.Long.toString(role))

        // If it doesn't exist
        if (permissions == null) {
            val permissionSet = ArrayList<String>()

            // Put it in local storage
            permissionData[role] = permissionSet

            // Put it in the database
            DatabaseManager.GUILD_DATA.setObject(guildID, java.lang.Long.toString(role), permissionSet)

            return permissionSet
        }

        // The permission set
        val permissionSet: ArrayList<String>

        // Try to cast it
        try {
            @Suppress("UNCHECKED_CAST")
            permissionSet = permissions as ArrayList<String>
        } catch (castException: ClassCastException) {
            // If it for some reason doesn't cast properly
            ChadInstance.getLogger().error("Permission set failed to cast to an array-list!", castException)
            return ArrayList()
        }

        // Add it to the local storage
        permissionData[role] = permissionSet

        // Return the retrieved set
        return permissionSet
    }

    /**
     * Adds a permission to a role
     *
     * @param role The role to add to
     * @param command The command to add
     * @return The error/success code
     */
    fun addPermissionToRole(role: Long, command: String): Int {
        // Get the role's permissions
        val permissionSet = getRolePermissions(role)

        // Make sure the command is an actual command
        if (!ChadVar.COMMANDS.containsKey(command) || ChadVar.COMMANDS[command]!!.commandCategory == Command.Category.DEVELOPER)
            return 3

        // Make sure it doesn't already have that permission
        if (permissionSet.contains(command))
            return 1

        // Add it
        permissionSet.add(command)

        // Re-add to hashmap
        permissionData[role] = permissionSet

        // Re-add to database
        DatabaseManager.GUILD_DATA.setObject(guildID, java.lang.Long.toString(role), permissionSet)

        return 0
    }

    /**
     * Removes a permission to role
     *
     * @param role The role to remove from
     * @param command The command to remove
     * @return The error/success code
     */
    fun removePermissionFromRole(role: Long, command: String): Int {
        // Get the role's permissions
        val permissionSet = getRolePermissions(role)

        // Make sure the command is an actual command
        if (!ChadVar.COMMANDS.containsKey(command) || ChadVar.COMMANDS[command]!!.commandCategory == Command.Category.DEVELOPER)
            return 3

        // If there's no commands at all
        if (permissionSet.isEmpty())
            return 4

        // Makes sure the role actually has the command
        if (!permissionSet.contains(command))
            return 1

        // Remove it
        permissionSet.remove(command)

        // Re-add to hashmap
        permissionData[role] = permissionSet

        // Re-add to database
        DatabaseManager.GUILD_DATA.setObject(guildID, java.lang.Long.toString(role), permissionSet)

        return 0
    }

    /**
     * Updates message sent statistics
     */
    fun messageSent() {
        messagesSent++
        guildData[DataType.MESSAGES_SENT] = messagesSent
    }

    /**
     * Updates command sent statistics
     */
    fun commandSent() {
        commandsSent++
        guildData[DataType.COMMANDS_SENT] = commandsSent
    }

    /**
     * Clears the guild's statistics
     */
    fun clearStatistics() {
        messagesSent = 0L
        commandsSent = 0L
        updateStatistics()
    }

    /**
     * Updates the statistics into the database
     */
    fun updateStatistics() {
        setObject(DataType.COMMANDS_SENT, commandsSent)
        setObject(DataType.MESSAGES_SENT, messagesSent)
    }
}
