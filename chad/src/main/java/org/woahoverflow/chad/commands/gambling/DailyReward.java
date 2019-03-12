package org.woahoverflow.chad.commands.gambling;

import org.jetbrains.annotations.NotNull;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Command.Class;
import org.woahoverflow.chad.framework.obj.Player;
import org.woahoverflow.chad.framework.obj.Player.DataType;
import org.woahoverflow.chad.framework.util.Util;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Gets a daily reward of 'money'
 *
 * @author sho
 */
public class DailyReward implements Class {
    @NotNull
    @Override
    public Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            Player player = PlayerHandler.getPlayer(e.getAuthor().getLongID());
            
            // If the user hasn't claimed the daily reward ever
            if (player.getObject(DataType.LAST_DAILY_REWARD).equals("none")) {
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

            if (difference < day) {
                messageHandler.sendError("You can only claim your reward once a day!\nTime left: " + Util.fancyDate(day - difference));
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

    @NotNull
    @Override
    public Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("dailyreward", "Claims your daily reward.");
        return Command.helpCommand(st, "Daily Reward", e);
    }
}
