package org.woahoverflow.chad.framework;

import com.mongodb.client.MongoCollection;
import java.util.concurrent.ConcurrentHashMap;
import org.bson.Document;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
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
        BALANCE, HEALTH, SWORD_HEALTH, SHIELD_HEALTH, MARRY_DATA, PROFILE_DESCRIPTION, PROFILE_TITLE, LAST_ATTACKED_TIME, LAST_ATTACKER, LAST_ATTACK_TIME, LAST_TARGET, LAST_CUDDLE_TIME
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

        playerData.put(DataType.LAST_ATTACKED_TIME, Util.getCurrentEpoch());
        playerData.put(DataType.LAST_ATTACKER, null);
        playerData.put(DataType.LAST_ATTACK_TIME, Util.getCurrentEpoch());
        playerData.put(DataType.LAST_TARGET, null);
        playerData.put(DataType.LAST_CUDDLE_TIME, Util.getCurrentEpoch());
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

        MongoCollection<Document> col = DatabaseHandler.handle.getSeparateCollection("user_data").getCollection();

        Document get = col.find(new Document("id", user)).first();

        if (get == null)
            return;

        col.updateOne(get, new Document("$set", new Document(dataType.toString().toLowerCase(), value)));

        PlayerHandler.handle.reAddPlayer(user);
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
}
