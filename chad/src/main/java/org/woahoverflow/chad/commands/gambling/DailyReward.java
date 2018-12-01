package org.woahoverflow.chad.commands.gambling;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.Command.Class;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class DailyReward implements Class {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // Makes sure the user has an account.
            if (!DatabaseHandler.handle.contains(e.getGuild(), e.getAuthor().getStringID() + "_balance"))
            {
                messageHandler.sendError("You don't have an account! \n Use `" + Chad
                    .getGuild(e.getGuild()).getDocument().getString("prefix") + "register` to get one!");
                return;
            }

            /*
            The format of the Daily Reward database entry is *userid*_ldr = MM.dd.yyyy
             */
            // If the user hasn't claimed the daily reward ever
            if (!DatabaseHandler.handle.contains(e.getGuild(), e.getAuthor().getStringID() + "_ldr"))
            {
                // Get the user's current balance
                long userBalance = (long) DatabaseHandler.handle.get(e.getGuild(), e.getAuthor().getStringID()+"_balance");

                // Adds the money
                DatabaseHandler.handle.set(e.getGuild(), e.getAuthor().getStringID()+"_balance", userBalance + 2000);

                // Updates the user's ldr
                DatabaseHandler.handle.set(e.getGuild(), e.getAuthor().getStringID()+"_ldr", DateTimeFormatter.ofPattern("MM&dd&yyyy").format(LocalDateTime.now()));

                // Send the message
                messageHandler.send("You claimed your daily reward of `2000`!", "Daily Reward");
                return;
            }

            // Gets the date of their last daily reward
            String lastDailyReward = DatabaseHandler.handle.getString(e.getGuild(), e.getAuthor().getStringID()+"_ldr");

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
            long currentBalance = (long) DatabaseHandler.handle.get(e.getGuild(), e.getAuthor().getStringID()+"_balance");

            // Updates the user's ldr
            DatabaseHandler.handle.set(e.getGuild(), e.getAuthor().getStringID()+"_ldr", DateTimeFormatter.ofPattern("MM&dd&yyyy").format(LocalDateTime.now()));

            // Adds the money
            DatabaseHandler.handle.set(e.getGuild(), e.getAuthor().getStringID()+"_balance", currentBalance + 2000);

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
