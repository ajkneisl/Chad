package org.woahoverflow.chad.framework.handle;

import java.util.concurrent.ConcurrentHashMap;
import org.bson.Document;
import org.woahoverflow.chad.framework.Player;

/**
 * Manages Player instances
 * @author sho, codebasepw
 * @since 0.7.0
 */
public class PlayerManager {
    public static final PlayerManager handle = new PlayerManager();

    private final ConcurrentHashMap<Long, Player> players = new ConcurrentHashMap<>();

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
        player.decrementPlayerHealth(damage);

        //registerPlayer(user, player);
        System.out.println("Player health value is: " + getPlayer(user).getPlayerHealth());
    }

    /**
     * Creates a player
     *
     * @param user The user's ID to register
     */
    public Player createPlayer(long user)
    {
        if (!userDataExists(user))
            return null;

        return createPlayer(user, 100, 100, 100);
    }

    /**
     * Creates a player with the specified default health values
     *
     * @param user The user's ID to register
     * @param playerHealth The user's starting playerHealth
     * @param swordHealth The user's starting swordHealth
     * @param shieldHealth The user's starting shieldHealth
     */
    public Player createPlayer(long user, int playerHealth, int swordHealth, int shieldHealth)
    {
        if (!userDataExists(user))
            return null;

        Document playerDocument = new Document();

        // The user's ID
        playerDocument.put("id", user);

        // The user's balance
        playerDocument.put("balance", Long.MAX_VALUE / 2);

        // The user's fight data
        playerDocument.put("swordHealth", swordHealth);
        playerDocument.put("shieldHealth", shieldHealth);
        playerDocument.put("playerHealth", playerHealth);

        // Insert the new player
        DatabaseHandler.handle.getSeparateCollection("user_data").getCollection().insertOne(playerDocument);

        // The player
        Player player = new Player(playerHealth, swordHealth, shieldHealth, Long.MAX_VALUE / 2);

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
