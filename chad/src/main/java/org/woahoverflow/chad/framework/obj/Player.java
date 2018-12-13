package org.woahoverflow.chad.framework.obj;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.woahoverflow.chad.framework.handle.database.DatabaseManager;
import org.woahoverflow.chad.framework.handle.PlayerHandler;

/**
 * @author sho, codebasepw
 * @since 0.7.0
 */
public class Player {

    /**
     * The user's ID
     */
    private final long user;

    /**
     * The types of data you can request from a player.
     */
    public enum DataType
    {
        // Other
        BALANCE, HEALTH, SWORD_HEALTH, SHIELD_HEALTH, LAST_ATTACKED_TIME, LAST_ATTACKER, LAST_ATTACK_TIME, LAST_TARGET, LAST_CUDDLE_TIME,

        // Profile
        PROFILE_UPVOTE, PROFILE_DOWNVOTE, PROFILE_DESCRIPTION, PROFILE_TITLE,

        // Data
        GUILD_DATA, MARRY_DATA, VOTE_DATA, LAST_DAILY_REWARD
    }

    /**
     * The player's full set of data
     */
    private ConcurrentHashMap<DataType, Object> playerData = new ConcurrentHashMap<>();

    /**
     * Default Constructor, sets it with default data.
     */
    public Player(long user)
    {
        this.user = user;
        playerData.put(DataType.SHIELD_HEALTH, 100);
        playerData.put(DataType.SWORD_HEALTH, 100);
        playerData.put(DataType.BALANCE, 0L);
        playerData.put(DataType.HEALTH, 100L);
        playerData.put(DataType.MARRY_DATA, "none&none");
        playerData.put(DataType.PROFILE_TITLE, "none");

        playerData.put(DataType.LAST_ATTACKED_TIME, System.currentTimeMillis());
        playerData.put(DataType.LAST_ATTACKER, "none");
        playerData.put(DataType.LAST_ATTACK_TIME, System.currentTimeMillis());
        playerData.put(DataType.LAST_TARGET, "none");
        playerData.put(DataType.LAST_CUDDLE_TIME, System.currentTimeMillis());

        playerData.put(DataType.PROFILE_DOWNVOTE, 0L);
        playerData.put(DataType.PROFILE_UPVOTE, 0L);

        playerData.put(DataType.VOTE_DATA, new ArrayList<>());
        playerData.put(DataType.GUILD_DATA, new ArrayList<>());
        playerData.put(DataType.LAST_DAILY_REWARD, "none");
    }

    /**
     * Sets a player with existing values
     *
     * @param playerData The preset values
     */
    public Player(ConcurrentHashMap<DataType, Object> playerData, long user)
    {
        this.playerData = playerData;
        this.user = user;
    }

    /**
     * Sets a data type's value.
     *
     * @param dataType The data type to set
     * @param value The value to set it to
     */
    public void setObject(DataType dataType, Object value)
    {
        playerData.put(dataType, value);

        DatabaseManager.USER_DATA.setObject(user, dataType.toString(), value);

        PlayerHandler.handle.refreshPlayer(user);
    }

    /**
     * Gets data from a data type.
     *
     * @param dataType The data type to retrieve
     * @return The retrieved data
     */
    public Object getObject(DataType dataType)
    {
        return playerData.get(dataType);
    }

    /**
     * Gets the user's ID
     *
     * @return The user's ID
     */
    public long getUserID()
    {
        return user;
    }
}
