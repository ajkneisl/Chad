package org.woahoverflow.chad.commands.fun;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Player;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public class Cuddle implements Command.Class {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            if (args.size() < 1)
            {
                messageHandler.sendError("You really want to cuddle with yourself? Ok I guess...");
                messageHandler.sendMessage("You cuddled yourself! (NO HEALTH INCREASE)");
                return;
            }

            if (args.size() == 1)
            {
                if (e.getMessage().getMentions().size() == 1)
                {
                    try
                    {
                        IUser partner = e.getMessage().getMentions().get(0);

                        Player partnerPlayer = PlayerHandler.handle.getPlayer(partner.getLongID());
                        Player authorPlayer = PlayerHandler.handle.getPlayer(e.getAuthor().getLongID());

                        long partnerLastCuddleTime = (long)partnerPlayer.getObject(Player.DataType.LAST_CUDDLE_TIME);
                        long authorLastCuddleTime = (long)authorPlayer.getObject(Player.DataType.LAST_CUDDLE_TIME);

                        // epoch sanity checks
                        long currentTimestamp = System.currentTimeMillis();
                        long searchTimestamp = authorLastCuddleTime;
                        long difference = Math.abs(currentTimestamp - searchTimestamp);
                        int hour = 60 * 60 * 1000;

                        if (difference < hour) {
                            messageHandler.sendError(String.format("Sorry, you need to wait an hour between cuddles! Time left: %s", TimeUnit.MILLISECONDS.toMinutes(hour - difference) + " minutes"));
                            return;
                        }

                        partnerPlayer.setObject(Player.DataType.LAST_CUDDLE_TIME, System.currentTimeMillis());
                        authorPlayer.setObject(Player.DataType.LAST_CUDDLE_TIME, System.currentTimeMillis());

                        String[] marriageData = ((String) authorPlayer.getObject(Player.DataType.MARRY_DATA)).split("&");

                        if (marriageData[0].equals(partner.getStringID()))
                        {
                            messageHandler.sendMessage("You cuddled your partner, " + partner.mention() + "! (+2 HEALTH)");

                            partnerPlayer.setObject(Player.DataType.HEALTH, (int)partnerPlayer.getObject(Player.DataType.HEALTH) + 2);
                            authorPlayer.setObject(Player.DataType.HEALTH, (int)authorPlayer.getObject(Player.DataType.HEALTH) + 2);
                        } else
                        {
                            messageHandler.sendMessage("You cuddled " + partner.mention() + "! (+1 HEALTH)");

                            partnerPlayer.setObject(Player.DataType.HEALTH, (int)partnerPlayer.getObject(Player.DataType.HEALTH) + 1);
                            authorPlayer.setObject(Player.DataType.HEALTH, (int)authorPlayer.getObject(Player.DataType.HEALTH) + 1);
                        }
                    } catch (RuntimeException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("cuddle <@user>", "Cuddle with another user.");
        st.put("cuddle", "Cuddle with yourself :)");
        return Command.helpCommand(st, "Cuddle", e);
    }
}
