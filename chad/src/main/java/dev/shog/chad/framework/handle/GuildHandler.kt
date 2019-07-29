package dev.shog.chad.framework.handle

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.document.Item
import dev.shog.chad.framework.handle.database.DatabaseManager
import dev.shog.chad.framework.handle.dynamo.DynamoDB
import dev.shog.chad.framework.obj.Guild
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Handles all guild instances
 *
 * @author sho
 */
object GuildHandler {
    /**
     * All guilds
     */
    private val guilds = ConcurrentHashMap<Long, Guild>()

    /**
     * Refreshes a guild into the hashmap
     *
     * @param guild The guild to refresh
     */
    @JvmStatic
    fun refreshGuild(guild: Long) {
        if (guilds.keys.contains(guild)) {
            guilds.remove(guild)
            guilds[guild] = getGuild(guild)
        }
    }

    /**
     * Removes a guild dataset from the database
     *
     * @param guild The user to remove
     */
    @JvmStatic
    fun removeGuild(guild: Long) {
        // Removes it from the hashmap
        guilds.remove(guild)

        // Removes it from the database
        DatabaseManager.GUILD_DATA.table.deleteItem("id", guild)
    }


    /**
     * Creates a guild instance
     *
     * @param guild The guild's id to register
     */
    @JvmStatic
    private fun createGuild(guild: Long): Guild {
        val guildDocument = Item()

        // The guild's ID
        guildDocument.with("id", guild)

        // Main
        guildDocument.with("prefix", "c!")

        // Logging
        guildDocument.with("logging", false)
        guildDocument.with("logging_channel", "none")

        // Messages
        guildDocument.with("join_message_on", false)
        guildDocument.with("leave_message_on", false)

        guildDocument.with("join_message_channel", "none")
        guildDocument.with("leave_message_channel", "none")

        guildDocument.with("ban_message", "`&user&` has been banned for `&reason&`!")
        guildDocument.with("kick_message", "`&user&` has been banned for `&reason&`!")
        guildDocument.with("leave_message", "`&user&` has left the server!")
        guildDocument.with("join_message", "`&user&` has been banned for `&reason&`!")

        guildDocument.with("ban_message_on", true)
        guildDocument.with("kick_message_on", true)

        // Statistics
        guildDocument.with("commands_sent", 0L)
        guildDocument.with("messages_sent", 0L)

        // Join Role
        guildDocument.with("join_role", "none")
        guildDocument.with("role_on_join", false)

        // Swearing
        guildDocument.with("swear_filter_message", "none")
        guildDocument.with("swear_filter", false)

        // Other
        guildDocument.with("disabled_categories", "[]")

        // Insert the new guild
        DatabaseManager.GUILD_DATA.table.putItem(guildDocument)

        // The new guild
        val guildInstance = parseGuild(guildDocument, guild)

        // Add it into the hash map
        guilds[guild] = guildInstance

        // Return the new guild
        return guildInstance
    }

    /**
     * Gets a guild's data from the database
     *
     * @param guildDataDocument The player to get
     * @return The guild retrieved
     */
    @JvmStatic
    private fun parseGuild(guildDataDocument: Item, guild: Long): Guild {
        val guildData = ConcurrentHashMap<Guild.DataType, Any>()

        // Sets the data
        for (type in Guild.DataType.values()) {
            guildData[type] = guildDataDocument.get(type.toString().toLowerCase()) ?: throw Exception("$type is null")
        }

        return Guild(guildData, guild)
    }

    /**
     * Gets a guild's instance
     *
     * @return The Guild's ID
     */
    @JvmStatic
    fun getGuild(guild: Long): Guild {
        // If the guild's in in the hash map, return it
        if (guildExists(guild)) return guilds[guild]!!

        val get = DatabaseManager.GUILD_DATA.getObject(guild)
        if (get != null) {
            val guildInstance = parseGuild(get, guild)

            guilds[guild] = guildInstance

            return parseGuild(get, guild)
        }

        // If it doesn't exist, make one
        val guildInstance = createGuild(guild)

        guilds[guild] = guildInstance

        return guildInstance
    }

    /**
     * If a guild exists within the hash-map
     *
     * @param guild The guild to chcek with
     * @return If it contains it
     */
    @JvmStatic
    fun guildExists(guild: Long): Boolean = guilds.containsKey(guild)
}
