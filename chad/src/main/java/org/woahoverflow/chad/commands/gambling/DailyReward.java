package org.woahoverflow.chad.commands.gambling;

import java.util.HashMap;
import java.util.List;

import java.util.concurrent.TimeUnit;
import org.woahoverflow.chad.framework.Util;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Command.Class;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Player;
import org.woahoverflow.chad.framework.obj.Player.DataType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

/**
 * Gets a daily reward of money
 *
 * @author sho
 * @since 0.6.3 B2
 */
public class DailyReward implements Class {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            Player player = PlayerHandler.handle.getPlayer(e.getAuthor().getLongID());
            
            // If the user hasn't claimed the daily reward ever
            if (player.getObject(DataType.LAST_DAILY_REWARD).equals("none"))
            {
                // Get the user's current balance
                long userBalance = (long) player.getObject(DataType.BALANCE);

                // Adds the money
                player.setObject(DataType.BALANCE, userBalance+2000);

                // Updates the user's ldr to the current time
                player.setObject(DataType.LAST_DAILY_REWARD, System.currentTimeMillis());

                // Send the message
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("You claimed your daily reward of `2000`!"));
                return;
            }

            // Gets the date of their last daily reward
            long lastDailyReward = (long) player.getObject(DataType.LAST_DAILY_REWARD); // TODO

            long difference = Util.howOld(lastDailyReward);
            int day = 24 * 60 * 60 * 1000;

            if (difference < day)
            {
                long hours = TimeUnit.MILLISECONDS.toHours(day - difference);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(day - difference) - (hours * 60);
                messageHandler.sendError("Sorry, you can only claim your reward once a day.! Time left: " + hours + " hours and " + minutes + " minutes.");
                return;
            }

            // Get the user's current balance
            long currentBalance = (long) player.getObject(DataType.BALANCE);

            // Adds the money
            player.setObject(DataType.BALANCE, currentBalance+2000);

            // Updates the user's ldr
            player.setObject(DataType.LAST_DAILY_REWARD, System.currentTimeMillis());

            // Send the message
            messageHandler.sendEmbed(new EmbedBuilder().withDesc("You claimed your daily reward of `2000`!"));
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("dailyreward", "Claims your daily reward.");
        return Command.helpCommand(st, "Daily Reward", e);
    }
}
