package com.jhobot.commands.function;

import com.jhobot.handle.Messages;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Purge implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            Messages m = new Messages(e.getChannel());
            if (!e.getAuthor().getPermissionsForGuild(e.getGuild()).contains(Permissions.ADMINISTRATOR))
            {
                m.sendError("You don't have permissions for this!");
                return;
            }
            if (args.size() == 0)
            {
                m.sendError("Invalid Arguments!");
                return;
            }

            boolean silent = false;
            if (e.getMessage().getContent().endsWith("-s"))
            {
                if (args.get(args.size()-1).endsWith("-s"))
                {
                    silent=true;
                    args.remove(args.size()-1);
                }
            }


            if (args.size() >= 2 && e.getMessage().getMentions().isEmpty())
            {
                IChannel ch = null;
                StringBuilder sb = new StringBuilder();
                int i = 0;
                for (String s : args)
                {
                    i++;
                    sb.append(s).append(" ");
                    if (!e.getGuild().getChannelsByName(sb.toString().trim()).isEmpty())
                        ch = e.getGuild().getChannelsByName(sb.toString().trim()).get(0);
                }


                if (i == 1)
                {
                    args.remove(0);
                }
                else {
                    args.remove(0);
                    i = i-1;
                    for (int i2=0;i2>i;i2++)
                        args.remove(i);
                }

                if (ch == null)
                {
                    m.sendError("Invalid Channel!");
                    return;
                }

                if (!ch.getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.MANAGE_MESSAGES))
                {
                    m.sendError("Bot doesn't have permissions for this!");
                    return;
                }

                try {
                    int i0 = Integer.parseInt(args.get(0));
                    if (i0 > 500)
                    {
                        m.sendError("You can only delete 500 messages at once!");
                        return;
                    }
                } catch (NumberFormatException throwaway)
                {
                    m.sendError("Not a valid integer!");
                    return;
                }


                final IChannel ch2 = ch;
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
                return;
            }

            if (args.size() >= 3 && !e.getMessage().getMentions().isEmpty())
            {
                IChannel ch = null;
                StringBuilder sb = new StringBuilder();
                int i = 0;
                for (String s : args)
                {
                    i++;
                    sb.append(s).append(" ");
                    if (!e.getGuild().getChannelsByName(sb.toString().trim()).isEmpty())
                        ch = e.getGuild().getChannelsByName(sb.toString().trim()).get(0);
                }


                if (i == 1)
                {
                    args.remove(0);
                }
                else {
                    args.remove(0);
                    i = i-1;
                    for (int i2=0;i2>i;i2++)
                        args.remove(i);
                }

                if (ch == null)
                {
                    m.sendError("Invalid Channel!");
                    return;
                }

                if (!ch.getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.MANAGE_MESSAGES))
                {
                    m.sendError("Bot doesn't have permissions for this!");
                    return;
                }
                args.remove(0);
                try {
                    int i0 = Integer.parseInt(args.get(0));
                    if (i0 > 500)
                    {
                        m.sendError("You can only delete 500 messages at once!");
                        return;
                    }
                } catch (NumberFormatException throwaway)
                {
                    m.sendError("Not a valid integer!");
                    return;
                }


                final IChannel ch2 = ch;
                RequestBuffer.request(() -> {
                    for (IMessage m9 : ch2.getMessageHistory(Integer.parseInt(args.get(0))))
                    {
                        if (m9.getAuthor() == e.getMessage().getMentions().get(0))
                            m9.delete();
                    }
                });
                IMessage m2 = RequestBuffer.request(() -> e.getChannel().sendMessage("Cleared `"+args.get(0)+"` messages from `"+e.getMessage().getMentions().get(0).getName()+"` in channel `"+ch2.getName()+"`")).get();
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
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("purge this", "Removes 500 messages from the current channel.");
        st.put("purge <channel name> <amount of messages>", "Removes a specific amount of messages from a defined channel.");
        st.put("For silent deletions", "Add -s to the end of the command.");
        return HelpHandler.helpCommand(st, "Purge", e);
    }
}
