package org.woahoverflow.chad.commands.admin;

import java.util.HashMap;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Player;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

/**
 * @author codebasepw
 * @since 0.7.0
 */
public class CreatePlayer implements Command.Class {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            if (args.size() != 3)
            {
                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }

            int playerHealth = Integer.parseInt(args.get(0));
            int swordHealth = Integer.parseInt(args.get(1));
            int armorHealth = Integer.parseInt(args.get(2));

            Player player = PlayerHandler.handle.createSetPlayer(e.getAuthor().getLongID(), playerHealth, swordHealth, armorHealth, 0L);

            playerHealth = (int)player.getObject(Player.DataType.HEALTH);
            swordHealth = (int)player.getObject(Player.DataType.SWORD_HEALTH);
            armorHealth = (int)player.getObject(Player.DataType.SHIELD_HEALTH);

            messageHandler.sendMessage("Created you a new player (" + playerHealth + ',' + swordHealth + ','
                + armorHealth + ')');
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("createplayer <health> <sword health> <shield health>", "Crates a new player.");
        return Command.helpCommand(st, "Create Player", e);
    }
}
