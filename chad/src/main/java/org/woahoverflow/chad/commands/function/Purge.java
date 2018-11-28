package org.woahoverflow.chad.commands.function;

import org.woahoverflow.chad.core.ChadBot;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class Purge implements Command.Class  {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // Makes sure the bot has permission to manage messages
            if (!ChadBot.cli.getOurUser().getPermissionsForGuild(e.getGuild()).contains(Permissions.MANAGE_MESSAGES))
            {
                messageHandler.sendError(MessageHandler.USER_NO_PERMISSION);
                return;
            }

            // Makes sure they've got the amount of messages they want to delete
            if (args.size() != 1)
            {
                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }

            // Gets the requested amount from the arguments and makes sure it's an actual integer
            int requestedAmount;
            try {
                requestedAmount = Integer.parseInt(args.get(0));
            } catch (NumberFormatException throwaway)
            {
                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }


            // Makes sure the amount isn't over 100
            if (requestedAmount > 100)
            {
                messageHandler.sendError("You can only delete 100 messages or less.");
                return;
            }

            // Deletes the user's message
            RequestBuffer.request(e.getMessage()::delete);

            // Deletes the messages from the channel
            RequestBuffer.request(() -> e.getChannel().getMessageHistory(Integer.parseInt(args.get(0))).bulkDelete());

            // Sends message confirming
            IMessage botConfirm = RequestBuffer.request(() -> e.getChannel().sendMessage("Cleared `"+args.get(0)+"` messages from `"+e.getChannel().getName()+"`")).get();

            // Waits 2 seconds, then deletes the bot's message
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            RequestBuffer.request(botConfirm::delete);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("purge <amount of messages>", "Removes a specific amount of messages from the current channel.");
        return Command.helpCommand(st, "Purge", e);
    }
}
