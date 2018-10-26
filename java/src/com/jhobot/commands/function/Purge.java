package com.jhobot.commands.function;

import com.jhobot.core.ChadBot;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class Purge implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        //return null;
        return() -> {
            if (!ChadBot.cli.getOurUser().getPermissionsForGuild(e.getGuild()).contains(Permissions.MANAGE_MESSAGES)) {
                new MessageHandler(e.getChannel()).sendError("Bot can't manage messages.");
                return;
            }

            boolean silent = false;

            if (e.getMessage().getContent().endsWith("-s")) {
                silent = true;
            }

            int requestedAmount = Integer.parseInt(args.get(0));
            if (requestedAmount > 100) {
                new MessageHandler(e.getChannel()).sendError("You can only delete 100 messages or less.");
                return;
            }

            final IChannel ch2 = e.getChannel();
            RequestBuffer.request(() -> ch2.getMessageHistory(Integer.parseInt(args.get(0))).bulkDelete());
            IMessage m2 = RequestBuffer.request(() -> e.getChannel().sendMessage("Cleared `"+args.get(0)+"` messages from `"+ch2.getName()+"`")).get();
            if (silent)
            {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                RequestBuffer.request(e.getMessage()::delete);
                RequestBuffer.request(m2::delete);
            }
        };
        };

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("purge this", "Removes 500 messages from the current channel.");
        st.put("purge <channel name> <amount of messages>", "Removes a specific amount of messages from a defined channel.");
        st.put("For silent deletions", "Add -s to the end of the command.");
        return HelpHandler.helpCommand(st, "Purge", e);
    }
}
