package org.woahoverflow.chad.framework.handle;

import com.mongodb.client.MongoCollection;
import java.util.concurrent.ConcurrentHashMap;
import org.bson.Document;
import org.woahoverflow.chad.framework.Player;
import org.woahoverflow.chad.framework.Player.DataType;

/**
 * Manages Player instances
 * @author sho, codebasepw
 * @since 0.7.0
 */
public class PlayerHandler {
    public static final PlayerHandler handle = new PlayerHandler();

    private final ConcurrentHashMap<Long, Player> players = new ConcurrentHashMap<>();

    public void reAddPlayer(long user)
    {
        if (players.keySet().contains(user))
            players.put(user, getPlayer(user));
    }
    /**
     * Attacks a player
     *
     * @param user The user to attack
     * @param damage The amount of damage to remove
     */
    public void attackPlayer(long user, int damage)
    {
        Player player = getPlayer(user);

        //unregisterPlayer(user);

        //registerPlayer(user, player);
    }

    /**
     * Creates a player
     *
     * @param user The user's ID to register
     */
    private Player createPlayer(long user)
    {
        Document playerDocument = new Document();

        // The user's ID
        playerDocument.put("id", user);

        // The user's balance
        playerDocument.put("balance", 0L);

        // The user's fight data
        playerDocument.put("sword_health", 100);
        playerDocument.put("shield_health", 100);
        playerDocument.put("health", 100);

        // Other default user data
        playerDocument.put("marry_data", "none&none");
        playerDocument.put("profile_description", "No description!");
        playerDocument.put("profile_title", "none");

        // Insert the new player
        DatabaseHandler.handle.getSeparateCollection("user_data").getCollection().insertOne(playerDocument);

        // The player
        Player player = parsePlayer(playerDocument, user);

        // Add it into the hash map
        players.put(user, player);

        // Return the new player
        return player;
    }

    /**
     * Gets a player's data from the database
     *
     * @param playerDataDocument The player to get
     * @return The player retrieved
     */
    private Player parsePlayer(Document playerDataDocument, long user)
    {
        final ConcurrentHashMap<DataType, Object> playerData = new ConcurrentHashMap<>();

        // Sets the data
        for (DataType type : DataType.values())
        {
            playerData.put(type, playerDataDocument.get(type.toString().toLowerCase()));
        }

        return new Player(playerData, user);
    }
    /**
     * Creates a player with the specified default health values (admin only, for petty cheating)
     *
     * @param user The user's ID to register
     * @param playerHealth The user's starting playerHealth
     * @param swordHealth The user's starting swordHealth
     * @param shieldHealth The user's starting shieldHealth
     */
    public Player createSetPlayer(long user, int playerHealth, int swordHealth, int shieldHealth, long balance)
    {
        if (!userDataExists(user))
            return null;

        Document playerDocument = new Document();

        // The user's ID
        playerDocument.put("id", user);

        // The user's balance
        playerDocument.put("balance", balance);

        // The user's fight data
        playerDocument.put("sword_health", swordHealth);
        playerDocument.put("shield_health", shieldHealth);
        playerDocument.put("health", playerHealth);

        // Other default user data
        playerDocument.put("marry_data", "none&none");
        playerDocument.put("profile_description", "No description!");
        playerDocument.put("profile_title", "none");

        // Insert the new player
        DatabaseHandler.handle.getSeparateCollection("user_data").getCollection().insertOne(playerDocument);

        // Creates the player with the new set data
        ConcurrentHashMap<DataType, Object> playerData = new ConcurrentHashMap<>();

        playerData.put(DataType.SWORD_HEALTH, swordHealth);
        playerData.put(DataType.SHIELD_HEALTH, shieldHealth);
        playerData.put(DataType.PROFILE_DESCRIPTION, "No description!");
        playerData.put(DataType.BALANCE, balance);
        playerData.put(DataType.HEALTH, playerHealth);
        playerData.put(DataType.MARRY_DATA, "none&none");
        playerData.put(DataType.PROFILE_TITLE, "none");

        Player player = new Player(playerData, user);

        // Add it into the hash map
        players.put(user, player);

        // Return the new player
        return player;
    }

    /**
     * Gets a player instance
     *
     * @return The user's Player instance
     */
    public Player getPlayer(long user)
    {
        // If the user's in in the hash map, return it
        if (players.containsKey(user))
        {
            return players.get(user);
        }

        if (userDataExists(user))
        {
            MongoCollection<Document> col = DatabaseHandler.handle.getSeparateCollection("user_data").getCollection();
            Document get = col.find(new Document("id", user)).first();

            if (get == null)
                return null;

            return parsePlayer(get, user);
        }

        // If it's not in there, create one
        return createPlayer(user);
    }

    /**
     * Checks if a user's data already exists within the database
     *
     * @param user The user to check
     * @return If it exists
     */
    public boolean userDataExists(long user)
    {
        Document get = DatabaseHandler.handle.getSeparateCollection("user_data").getCollection().find(new Document("id", user)).first();
        return get != null;
    }
}