package dev.shog.chad.framework.handle

import org.bson.Document
import dev.shog.chad.framework.handle.database.DatabaseManager
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
        dev.shog.chad.framework.handle.database.DatabaseManager.GUILD_DATA.removeDocument(guild)
    }


    /**
     * Creates a guild instance
     *
     * @param guild The guild's id to register
     */
    @JvmStatic
    private fun createGuild(guild: Long): Guild {
        val guildDocument = Document()

        // The guild's ID
        guildDocument["id"] = guild

        // Main
        guildDocument["prefix"] = "c!"

        // Logging
        guildDocument["logging"] = false
        guildDocument["logging_channel"] = "none"

        // Messages
        guildDocument["join_message_on"] = false
        guildDocument["leave_message_on"] = false

        guildDocument["join_message_channel"] = "none"
        guildDocument["leave_message_channel"] = "none"

        guildDocument["ban_message"] = "`&user&` has been banned for `&reason&`!"
        guildDocument["kick_message"] = "`&user&` has been banned for `&reason&`!"
        guildDocument["leave_message"] = "`&user&` has left the server!"
        guildDocument["join_message"] = "`&user&` has been banned for `&reason&`!"

        guildDocument["ban_message_on"] = true
        guildDocument["kick_message_on"] = true

        // Statistics
        guildDocument["messages_sent"] = 0L
        guildDocument["commands_sent"] = 0L

        // Join Role
        guildDocument["role_on_join"] = false
        guildDocument["join_role"] = "none"

        // Swearing
        guildDocument["swear_filter"] = false
        guildDocument["swear_filter_message"] = "none"

        // Other
        guildDocument["disabled_categories"] = ArrayList<Any>()

        // Insert the new guild
        dev.shog.chad.framework.handle.database.DatabaseManager.GUILD_DATA.collection.insertOne(guildDocument)

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
    private fun parseGuild(guildDataDocument: Document, guild: Long): Guild {
        val guildData = ConcurrentHashMap<Guild.DataType, Any>()

        // Sets the data
        for (type in Guild.DataType.values()) {
            guildData[type] = guildDataDocument[type.toString().toLowerCase()]!!
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

        if (dev.shog.chad.framework.handle.database.DatabaseManager.GUILD_DATA.documentExists(guild)) {
            val get = dev.shog.chad.framework.handle.database.DatabaseManager.GUILD_DATA.collection.find(Document("id", guild)).first()!!

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
    fun guildExists(guild: Long): Boolean {
        return guilds.containsKey(guild)
    }

    /**
     * If a guild exists within the database
     *
     * @param guild The guild's ID
     * @return If it exists
     */

    @JvmStatic
    fun guildDataExists(guild: Long): Boolean {
        val get = dev.shog.chad.framework.handle.database.DatabaseManager.GUILD_DATA.collection.find(Document("id", guild)).first()
        return get != null
    }
}
