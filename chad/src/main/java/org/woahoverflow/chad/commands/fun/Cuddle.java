package org.woahoverflow.chad.commands.fun;

import java.util.HashMap;
import java.util.List;

import javafx.scene.chart.XYChart;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Player;
import org.woahoverflow.chad.framework.Util;
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
                    IUser partner = e.getMessage().getMentions().get(0);

                    Player partnerPlayer = PlayerHandler.handle.getPlayer(partner.getLongID());
                    Player authorPlayer = PlayerHandler.handle.getPlayer(e.getAuthor().getLongID());

                    long partnerLastCuddleTime = (long)partnerPlayer.getObject(Player.DataType.LAST_CUDDLE_TIME);
                    long authorLastCuddleTime = (long)authorPlayer.getObject(Player.DataType.LAST_CUDDLE_TIME);

                    // epoch sanity checks
                    if ((authorLastCuddleTime - Util.getCurrentEpoch()) > 3600)
                    {
                        messageHandler.sendError(String.format("Sorry, you need to wait an hour between cuddles! Time left: %s", authorLastCuddleTime - Util.getCurrentEpoch()));
                        return;
                    }

                    messageHandler.sendMessage("You cuddled " + partner.mention() + "! (+1 HEALTH)");
                    long currentTime = Util.getCurrentEpoch();
                    messageHandler.sendError(String.format(
                            "Partner data" +
                            "\n`lastCuddleTime`: %s" +
                            "\n`currentTime`: %s" +
                            "\n`Difference`: %s" +
                            "\n\nAuthor data" +
                            "\n`lastCuddleTime`: %s" +
                            "\n`currentTime`: %s" +
                            "\n`Difference`: %s",
                            partnerLastCuddleTime,
                            currentTime,
                            partnerLastCuddleTime - currentTime,
                            authorLastCuddleTime,
                            currentTime,
                            authorLastCuddleTime - currentTime));

                    partnerPlayer.setObject(Player.DataType.LAST_CUDDLE_TIME, Util.getCurrentEpoch());
                    authorPlayer.setObject(Player.DataType.LAST_CUDDLE_TIME, Util.getCurrentEpoch());

                    partnerPlayer.setObject(Player.DataType.HEALTH, (int)partnerPlayer.getObject(Player.DataType.HEALTH) + 1);
                    authorPlayer.setObject(Player.DataType.HEALTH, (int)authorPlayer.getObject(Player.DataType.HEALTH) + 1);
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
