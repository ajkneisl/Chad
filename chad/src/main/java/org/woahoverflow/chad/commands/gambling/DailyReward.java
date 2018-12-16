package org.woahoverflow.chad.commands.gambling;

import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.handle.PlayerHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Command.Class;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Player;
import org.woahoverflow.chad.framework.obj.Player.DataType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

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

            /*
            The format of the Daily Reward database entry is *userid*_ldr = MM.dd.yyyy
             */
            // If the user hasn't claimed the daily reward ever
            if (player.getObject(DataType.LAST_DAILY_REWARD).equals("none"))
            {
                // Get the user's current balance
                long userBalance = (long) player.getObject(DataType.BALANCE);

                // Adds the money
                player.setObject(DataType.BALANCE, userBalance+2000);

                // Updates the user's ldr
                player.setObject(DataType.LAST_DAILY_REWARD, "" /* TODO */);

                // Send the message
                messageHandler.send("You claimed your daily reward of `2000`!", "Daily Reward");
                return;
            }

            // Gets the date of their last daily reward
            String lastDailyReward = (String) player.getObject(DataType.LAST_DAILY_REWARD); // TODO

            // Just so IntelliJ stops yelling at me
            if (lastDailyReward == null)
            {
                messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                return;
            }

            // TODO return in this if the user can't claim it
//            // Makes sure it's not the same day as they last claimed it
//            if (DateTimeFormatter.ofPattern("MM&dd&yyyy").format(LocalDateTime.now()).equalsIgnoreCase(lastDailyReward))
//            {
//                // Get the day, year and month
//                String[] dayYearMonth = lastDailyReward.split("&");
//
//                // Build the date they'll be able to get it next
//                String newTime;
//                try {
//                    newTime = dayYearMonth[0] + '/';
//
//                    int newDay = Integer.parseInt(dayYearMonth[1])+1;
//
//                    newTime += !(Integer.parseInt(dayYearMonth[1]) > 9) ? "0" + newDay : newDay;
//                    newTime += '/' + dayYearMonth[2];
//                } catch (NumberFormatException throwaway) {
//                    messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
//                    return;
//                }
//
//                // Sends the message
//                messageHandler.sendError("You can't claim your reward until `"+ newTime +"`!");
//                return;
//            }

            // Get the user's current balance
            long currentBalance = (long) player.getObject(DataType.BALANCE);

            // Adds the money
            player.setObject(DataType.BALANCE, currentBalance+2000);

            // Updates the user's ldr
            player.setObject(DataType.LAST_DAILY_REWARD, "" /* TODO */);

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
