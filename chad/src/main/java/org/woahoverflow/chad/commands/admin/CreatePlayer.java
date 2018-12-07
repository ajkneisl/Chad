package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Player;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

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

            Player player = PlayerManager.INSTANCE.createNewPlayer(e.getAuthor(), playerHealth, swordHealth, armorHealth);

            messageHandler.sendError("Player health: " + player.getPlayerHealth() + "\nSword health: " + player.getSwordHealth() + "\nArmor health: " + player.getArmorHealth() + "\nRunning test...");

            PlayerManager.INSTANCE.attackPlayer(e.getAuthor(), 1);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        return null;
    }
}
