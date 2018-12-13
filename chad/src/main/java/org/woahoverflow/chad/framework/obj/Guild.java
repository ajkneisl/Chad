package org.woahoverflow.chad.framework.obj;

import java.util.concurrent.ConcurrentHashMap;
import org.woahoverflow.chad.framework.handle.database.DatabaseManager;

public class Guild
{

    /**
     * The guild's ID
     */
    private final long guild;

    /**
     * The types of data you can request from a guild.
     */
    public enum DataType
    {
        // Main
        PREFIX,

        // Logging
        LOGGING, LOGGING_CHANNEL,

        // Messages
        JOIN_MESSAGE_ON, LEAVE_MESSAGE_ON, JOIN_MESSAGE_CHANNEL, LEAVE_MESSAGE_CHANNEL, BAN_MESSAGE_ON, KICK_MESSAGE_ON,
        BAN_MESSAGE, KICK_MESSAGE, JOIN_MESSAGE, LEAVE_MESSAGE,

        // Join Role
        ROLE_ON_JOIN, JOIN_ROLE,

        // Swearing
        SWEAR_FILTER, SWEAR_FILTER_MESSAGE,

        // Other
        ALLOW_COMMUNITY_FEATURES
    }

    /**
     * The guild's full set of data
     */
    private ConcurrentHashMap<DataType, Object> guildData = new ConcurrentHashMap<>();

    /**
     * Default Constructor, sets it with default data.
     */
    public Guild(long guild)
    {
        this.guild = guild;

        // Main
        guildData.put(DataType.PREFIX, "c!");

        // Logging
        guildData.put(DataType.LOGGING, false);
        guildData.put(DataType.LOGGING_CHANNEL, "none");

        // Messages
        guildData.put(DataType.JOIN_MESSAGE, "`&user&` has joined the server!");
        guildData.put(DataType.JOIN_MESSAGE_ON, false);
        guildData.put(DataType.JOIN_MESSAGE_CHANNEL, "none");

        guildData.put(DataType.LEAVE_MESSAGE, "`&user` has left the server!");
        guildData.put(DataType.LEAVE_MESSAGE_ON, false);
        guildData.put(DataType.LEAVE_MESSAGE_CHANNEL, "none");

        guildData.put(DataType.BAN_MESSAGE, "`&user& has been banned for `&reason&`!");
        guildData.put(DataType.KICK_MESSAGE, "`&user& has been kicked for `&reason&`!");

        guildData.put(DataType.BAN_MESSAGE_ON, true);
        guildData.put(DataType.KICK_MESSAGE_ON, true);

        // Join Role
        guildData.put(DataType.JOIN_ROLE, "none");
        guildData.put(DataType.ROLE_ON_JOIN, false);

        // Swearing
        guildData.put(DataType.SWEAR_FILTER, false);
        guildData.put(DataType.SWEAR_FILTER_MESSAGE, "Stop swearing, `&user&`!");

        // Other
        guildData.put(DataType.ALLOW_COMMUNITY_FEATURES, true);
    }

    /**
     * Sets a guild with existing values
     *
     * @param guildData The preset values
     */
    public Guild(ConcurrentHashMap<DataType, Object> guildData, long guild)
    {
        this.guildData = guildData;
        this.guild = guild;
    }

    /**
     * Sets a data type's value.
     *
     * @param dataType The data type to set
     * @param value The value to set it to
     */
    public void setObject(DataType dataType, Object value)
    {
        guildData.put(dataType, value);

        DatabaseManager.USER_DATA.setObject(guild, dataType.toString(), value);
    }

    /**
     * Gets data from a data type.
     *
     * @param dataType The data type to retrieve
     * @return The retrieved data
     */
    public Object getObject(DataType dataType)
    {
        return guildData.get(dataType);
    }

    /**
     * Gets the guild's ID
     *
     * @return The guild's ID
     */
    public long getGuildID()
    {
        return guild;
    }
}
