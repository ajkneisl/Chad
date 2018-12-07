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

    private static final ConcurrentHashMap<Long, Player> players = new ConcurrentHashMap<>();

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
    private static Player createPlayer(long user)
    {
        Document playerDocument = new Document();

        // The user's ID
        playerDocument.put("id", user);

        // The user's balance
        playerDocument.put("balance", 0L);

        // The user's fight data
        playerDocument.put("swordHealth", 10);
        playerDocument.put("shieldHealth", 10);
        playerDocument.put("playerHealth", 10);

        // Insert the new player
        DatabaseHandler.handle.getSeparateCollection("user_data").getCollection().insertOne(playerDocument);

        // The player
        Player player = new Player(10, 10, 10, 0L);

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
}
