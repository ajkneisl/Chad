package org.woahoverflow.chad.framework.handle;

import java.util.concurrent.ConcurrentHashMap;
import org.woahoverflow.chad.framework.Player;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    public static final PlayerManager handle = new PlayerManager();

    private static final ConcurrentHashMap<IUser, Player> players = new ConcurrentHashMap<>();

    public Player createNewPlayer(IUser user, int playerHealth, int swordHealth, int armorHealth)
    {
        Player player = new Player(playerHealth, swordHealth, armorHealth);

        registerPlayer(user, player);

        return player;
    }

    public void registerPlayer(IUser user, Player player)
    {
        players.put(user, player);
    }

    public void unregisterPlayer(IUser user)
    {
        players.remove(user);
    }

    public Player getRegisteredPlayer(IUser user)
    {
        return players.get(user);
    }

    public void attackPlayer(IUser user, int damage)
    {
        Player player = getRegisteredPlayer(user);
        //unregisterPlayer(user);
        player.decrementPlayerHealth(damage);
        //registerPlayer(user, player);
        System.out.println("Player health value is: " + getRegisteredPlayer(user).getPlayerHealth());
    }
}
