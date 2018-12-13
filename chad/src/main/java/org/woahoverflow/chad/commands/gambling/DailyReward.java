package org.woahoverflow.chad.commands.gambling;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Command.Class;
import org.woahoverflow.chad.framework.handle.database.DatabaseManager;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * @author sho
 * @since 0.6.3 B2
 */
public class DailyReward implements Class {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            /*
            The format of the Daily Reward database entry is *userid*_ldr = MM.dd.yyyy
             */
            // If the user hasn't claimed the daily reward ever
            if (!DatabaseManager.handle.contains(e.getGuild(), e.getAuthor().getStringID() + "_ldr"))
            {
                // Get the user's current balance
                long userBalance = (long) DatabaseManager.handle.get(e.getGuild(), e.getAuthor().getStringID()+"_balance");

                // Adds the money
                DatabaseManager.handle.set(e.getGuild(), e.getAuthor().getStringID()+"_balance", userBalance + 2000);

                // Updates the user's ldr
                DatabaseManager.handle.set(e.getGuild(), e.getAuthor().getStringID()+"_ldr", DateTimeFormatter.ofPattern("MM&dd&yyyy").format(LocalDateTime.now()));

                // Send the message
                messageHandler.send("You claimed your daily reward of `2000`!", "Daily Reward");
                return;
            }

            // Gets the date of their last daily reward
            String lastDailyReward = DatabaseManager.handle.getString(e.getGuild(), e.getAuthor().getStringID()+"_ldr");

            // Just so IntelliJ stops yelling at me
            if (lastDailyReward == null)
            {
                messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                return;
            }

            // Makes sure it's not the same day as they last claimed it
            if (DateTimeFormatter.ofPattern("MM&dd&yyyy").format(LocalDateTime.now()).equalsIgnoreCase(lastDailyReward))
            {
                // Get the day, year and month
                String[] dayYearMonth = lastDailyReward.split("&");

                // Build the date they'll be able to get it next
                String newTime;
                try {
                    newTime = dayYearMonth[0] + '/';

                    int newDay = Integer.parseInt(dayYearMonth[1])+1;

                    newTime += !(Integer.parseInt(dayYearMonth[1]) > 9) ? "0" + newDay : newDay;
                    newTime += '/' + dayYearMonth[2];
                } catch (NumberFormatException throwaway) {
                    messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                    return;
                }

                // Sends the message
                messageHandler.sendError("You can't claim your reward until `"+ newTime +"`!");
                return;
            }

            // Get the user's current balance
            long currentBalance = (long) DatabaseManager.handle.get(e.getGuild(), e.getAuthor().getStringID()+"_balance");

            // Updates the user's ldr
            DatabaseManager.handle.set(e.getGuild(), e.getAuthor().getStringID()+"_ldr", DateTimeFormatter.ofPattern("MM&dd&yyyy").format(LocalDateTime.now()));

            // Adds the money
            DatabaseManager.handle.set(e.getGuild(), e.getAuthor().getStringID()+"_balance", currentBalance + 2000);

            // Send the message
            messageHandler.send("You claimed your daily reward of `2000`!", "Daily Reward");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("dailyreward", "Claims your daily reward.");
        return Command.helpCommand(st, "Daily Reward", e);
    }
}
