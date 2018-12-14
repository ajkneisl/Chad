package org.woahoverflow.chad.framework.handle;

import java.util.concurrent.ConcurrentHashMap;
import org.bson.Document;
import org.woahoverflow.chad.framework.handle.database.DatabaseManager;
import org.woahoverflow.chad.framework.obj.Guild;

public class GuildHandler
{

    /**
     * Static Instance
     */
    public static final GuildHandler handle = new GuildHandler();

    /**
     * All guilds
     */
    private final ConcurrentHashMap<Long, Guild> guilds = new ConcurrentHashMap<>();

    /**
     * Refreshes a guild into the hashmap
     *
     * @param guild The guild to refresh
     */
    public void refreshGuild(long guild)
    {
        if (guilds.keySet().contains(guild))
        {
            guilds.remove(guild);
            guilds.put(guild, getGuild(guild));
        }
    }

    /**
     * Removes a guild dataset from the database
     *
     * @param guild The user to remove
     */
    public void removeGuild(long guild)
    {
        // Removes it from the hashmap
        guilds.remove(guild);

        // Removes it from the database
        DatabaseManager.GUILD_DATA.removeDocument(guild);
    }


    /**
     * Creates a guild instance
     *
     * @param guild The guild's id to register
     */
    private Guild createPlayer(long guild)
    {
        Document guildDocument = new Document();

        // The guild's ID
        guildDocument.put("id", guild);

        // Main
        guildDocument.put("prefix", "c!");

        // Logging
        guildDocument.put("logging", false);
        guildDocument.put("logging_channel", "none");

        // Messages
        guildDocument.put("join_message_on", false);
        guildDocument.put("leave_message_on", false);

        guildDocument.put("join_message_channel", "none");
        guildDocument.put("leave_message_channel", "none");

        guildDocument.put("ban_message", "`&user&` has been banned for `&reason&`!");
        guildDocument.put("kick_message", "`&user&` has been banned for `&reason&`!");
        guildDocument.put("leave_message", "`&user&` has left the server!");
        guildDocument.put("join_message", "`&user&` has been banned for `&reason&`!");

        guildDocument.put("ban_message_on", true);
        guildDocument.put("kick_message_on", true);

        // Statistics
        guildDocument.put("messages_sent", 0L);
        guildDocument.put("commands_sent", 0L);

        // Join Role
        guildDocument.put("role_on_join", false);
        guildDocument.put("join_role", "none");

        // Swearing
        guildDocument.put("swear_filter", false);
        guildDocument.put("swear_filter_message", "none");

        // Other
        guildDocument.put("allow_community_features", true);

        // Insert the new guild
        DatabaseManager.GUILD_DATA.collection.insertOne(guildDocument);

        // The new guild
        Guild guildInstance = parseGuild(guildDocument, guild);

        // Add it into the hash map
        guilds.put(guild, guildInstance);

        // Return the new guild
        return guildInstance;
    }

    /**
     * Gets a guild's data from the database
     *
     * @param guildDataDocument The player to get
     * @return The guild retrieved
     */
    private Guild parseGuild(Document guildDataDocument, long guild)
    {
        final ConcurrentHashMap<Guild.DataType, Object> guildData = new ConcurrentHashMap<>();

        // Sets the data
        for (Guild.DataType type : Guild.DataType.values())
        {
            guildData.put(type, guildDataDocument.get(type.toString().toLowerCase()));
        }

        return new Guild(guildData, guild);
    }

    /**
     * Gets a guild's instance
     *
     * @return The Guild's ID
     */
    public Guild getGuild(long guild)
    {
        // If the guild's in in the hash map, return it
        if (guilds.containsKey(guild))
        {
            return guilds.get(guild);
        }

        if (DatabaseManager.GUILD_DATA.documentExists(guild))
        {
            Document get = DatabaseManager.GUILD_DATA.collection.find(new Document("id", guild)).first();

            if (get == null)
                return null;

            Guild guildInstance = parseGuild(get, guild);

            guilds.put(guild, guildInstance);

            return parseGuild(get, guild);
        }

        // If it doesn't exist, make one
        Guild guildInstance = createPlayer(guild);

        guilds.put(guild, guildInstance);

        return guildInstance;
    }
}
