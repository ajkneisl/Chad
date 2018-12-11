package org.woahoverflow.chad.commands.fight;

import java.util.HashMap;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Player;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class ViewPlayer implements Command.Class {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            if (args.size() < 1)
            {
                Player player = PlayerHandler.handle.getPlayer(e.getAuthor().getLongID());
                int playerHealth = (int)player.getObject(Player.DataType.HEALTH);
                int swordHealth = (int)player.getObject(Player.DataType.SWORD_HEALTH);
                int shieldHealth = (int)player.getObject(Player.DataType.SHIELD_HEALTH);
                String playerContent = "Player data for " + e.getAuthor().mention();
                playerContent += "\n`Health`: " + playerHealth;
                playerContent += "\n`Sword Health`: " + swordHealth;
                playerContent += "\n`Shield Health`: " + shieldHealth;
                messageHandler.sendMessage(playerContent);
            } else if (args.size() == 1)
            {
                if (e.getMessage().getMentions().size() < 1)
                {
                    messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                    return;
                }

                Player player = PlayerHandler.handle.getPlayer(e.getMessage().getMentions().get(0).getLongID());
                int playerHealth = (int)player.getObject(Player.DataType.HEALTH);
                int swordHealth = (int)player.getObject(Player.DataType.SWORD_HEALTH);
                int shieldHealth = (int)player.getObject(Player.DataType.SHIELD_HEALTH);
                String playerContent = "Player data for " + e.getMessage().getMentions().get(0).mention();
                playerContent += "\n`Health`: " + playerHealth;
                playerContent += "\n`Sword Health`: " + swordHealth;
                playerContent += "\n`Shield Health`: " + shieldHealth;
                messageHandler.sendMessage(playerContent);
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        //st.put("", "");
        return Command.helpCommand(st, "View Player", e);
    }
}
