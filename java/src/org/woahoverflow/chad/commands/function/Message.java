package org.woahoverflow.chad.commands.function;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;

public class Message implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
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

            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("join"))
            {
                StringBuilder sb = new StringBuilder();
                args.remove(0);
                for (String s : args)
                {
                    sb.append(s).append(" ");
                }

                String old = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "join_message");
                ChadVar.DATABASE_HANDLER.set(e.getGuild(), "join_message", sb.toString().trim());
                m.sendMessage("Set the guild's join message to `"+sb.toString().trim().replaceAll("```", "<large code-block>").replaceAll("`", "<small code-block>")+"`");
                m.sendConfigLog("Join Message", sb.toString().trim(), old, e.getAuthor(), e.getGuild());
                return;
            }

            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("leave"))
            {
                StringBuilder sb = new StringBuilder();
                args.remove(0);
                for (String s : args)
                {
                    sb.append(s).append(" ");
                }

                String old = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "leave_message");
                ChadVar.DATABASE_HANDLER.set(e.getGuild(), "leave_message", sb.toString().trim());
                m.sendMessage("Set the guild's leave message to `"+sb.toString().trim().replaceAll("```", "<large code-block>").replaceAll("`", "<small code-block>")+"`");
                m.sendConfigLog("Leave Message", sb.toString().trim(), old, e.getAuthor(), e.getGuild());
                return;
            }
            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("ban"))
            {
                StringBuilder sb = new StringBuilder();
                args.remove(0);
                for (String s : args)
                {
                    sb.append(s).append(" ");
                }

                String old = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "ban_message");
                ChadVar.DATABASE_HANDLER.set(e.getGuild(), "ban_message", sb.toString().trim());
                m.sendMessage("Set the guild's ban message to `"+sb.toString().trim().replaceAll("```", "<large code-block>").replaceAll("`", "<small code-block>")+"`");
                m.sendConfigLog("Ban Message", sb.toString().trim(), old, e.getAuthor(), e.getGuild());
                return;
            }

            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("kick"))
            {
                StringBuilder sb = new StringBuilder();
                args.remove(0);
                for (String s : args)
                {
                    sb.append(s).append(" ");
                }

                String old = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "kick_message");
                ChadVar.DATABASE_HANDLER.set(e.getGuild(), "kick_message", sb.toString().trim());
                m.sendMessage("Set the guild's kick message to `"+sb.toString().trim().replaceAll("```", "<large code-block>").replaceAll("`", "<small code-block>")+"`");
                m.sendConfigLog("Kick Message", sb.toString().trim(), old, e.getAuthor(), e.getGuild());
                return;
            }

            if (args.size() == 3 && args.get(0).equalsIgnoreCase("toggle"))
            {
                boolean set;
                if (args.get(2).equalsIgnoreCase("false"))
                    set = false;
                else if (args.get(2).equalsIgnoreCase("true"))
                    set = true;
                else
                {
                    m.sendError("Not a valid boolean!");
                    return;
                }

                if (args.get(1).equalsIgnoreCase("join"))
                {
                    m.sendMessage("Set the guild's join message toggle to `"+set+"`");
                    m.sendConfigLog("Kick Message Toggle", Boolean.toString(set), Boolean.toString(ChadVar.DATABASE_HANDLER.getBoolean(e.getGuild(),"join_msg_on")), e.getAuthor(), e.getGuild());
                    ChadVar.DATABASE_HANDLER.set(e.getGuild(), "join_msg_on", set);
                    return;
                }
                if (args.get(1).equalsIgnoreCase("ban"))
                {
                    m.sendMessage("Set the guild's ban message toggle to `"+set+"`");
                    m.sendConfigLog("Kick Message Toggle", Boolean.toString(set), Boolean.toString(ChadVar.DATABASE_HANDLER.getBoolean(e.getGuild(),"ban_msg_on")), e.getAuthor(), e.getGuild());
                    ChadVar.DATABASE_HANDLER.set(e.getGuild(), "ban_msg_on", set);
                    return;
                }
                if (args.get(1).equalsIgnoreCase("kick"))
                {
                    m.sendMessage("Set the guild's kick message toggle to `"+set+"`");
                    m.sendConfigLog("Kick Message Toggle", Boolean.toString(set), Boolean.toString(ChadVar.DATABASE_HANDLER.getBoolean(e.getGuild(),"kick_msg_on")), e.getAuthor(), e.getGuild());
                    ChadVar.DATABASE_HANDLER.set(e.getGuild(), "kick_msg_on", set);
                    return;
                }
                if (args.get(1).equalsIgnoreCase("leave"))
                {
                    m.sendMessage("Set the guild's leave message toggle to `"+set+"`");
                    m.sendConfigLog("Leave Message Toggle", Boolean.toString(set), Boolean.toString(ChadVar.DATABASE_HANDLER.getBoolean(e.getGuild(),"leave_msg_on")), e.getAuthor(), e.getGuild());
                    ChadVar.DATABASE_HANDLER.set(e.getGuild(), "leave_msg_on", set);
                    return;
                }
                m.sendError("Invalid Type!");
            }

            if (args.size() >= 3 && args.get(0).equalsIgnoreCase("setchannel"))
            {
                if (args.get(1).equalsIgnoreCase("join"))
                {
                    String newVal;
                    String oldName;
                    IChannel newChannel;
                    IChannel oldChannel;
                    String channelString = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "join_message_ch");

                    if (channelString.equalsIgnoreCase("none"))
                        oldName = "none";
                    else
                    {
                        oldChannel = RequestBuffer.request(() -> e.getGuild().getChannelByID(Long.parseLong(channelString))).get();
                        if (oldChannel.isDeleted())
                            oldName = "Deleted Channel";
                        else
                            oldName = oldChannel.getName();
                    }

                    StringBuilder b = new StringBuilder();
                    args.remove(0);
                    args.remove(0);
                    for (String s : args)
                    {
                        b.append(s).append(" ");
                    }
                    newVal = b.toString().trim();
                    List<IChannel> l = RequestBuffer.request(() -> e.getGuild().getChannelsByName(newVal)).get();

                    if (l.isEmpty())
                    {
                        m.sendError("Invalid Channel!");
                        return;
                    }

                    newChannel = l.get(0);

                    ChadVar.DATABASE_HANDLER.set(e.getGuild(), "join_message_ch", newChannel.getStringID());
                    m.sendMessage("Set the guild's join channel to `"+newChannel.getName()+"`");
                    m.sendConfigLog("Join Message Channel", newChannel.getName(), oldName, e.getAuthor(), e.getGuild());
                    return;
                }

                if (args.get(1).equalsIgnoreCase("leave"))
                {
                    String newVal;
                    String oldName;
                    IChannel newChannel;
                    IChannel oldChannel;
                    String channelString = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "leave_message_ch");

                    if (channelString.equalsIgnoreCase("none"))
                        oldName = "none";
                    else
                    {
                        oldChannel = RequestBuffer.request(() -> e.getGuild().getChannelByID(Long.parseLong(channelString))).get();
                        if (oldChannel.isDeleted())
                            oldName = "Deleted Channel";
                        else
                            oldName = oldChannel.getName();
                    }

                    StringBuilder b = new StringBuilder();
                    args.remove(0);
                    args.remove(0);
                    for (String s : args)
                    {
                        b.append(s).append(" ");
                    }
                    newVal = b.toString().trim();
                    List<IChannel> l = RequestBuffer.request(() -> e.getGuild().getChannelsByName(newVal)).get();

                    if (l.isEmpty())
                    {
                        m.sendError("Invalid Channel!");
                        return;
                    }

                    newChannel = l.get(0);

                    ChadVar.DATABASE_HANDLER.set(e.getGuild(), "leave_message_ch", newChannel.getStringID());
                    m.sendMessage("Set the guild's leave channel to `"+newChannel.getName()+"`");
                    m.sendConfigLog("Leave Message Channel", newChannel.getName(), oldName, e.getAuthor(), e.getGuild());
                    return;
                }
                m.sendError("Invalid Type!");
            }
            m.sendError("Invalid Arguments!");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("im join <message>", "Sets the join message.");
        st.put("im leave <message>", "Sets the leave message.");
        st.put("im ban <message>", "Sets the ban message.");
        st.put("im kick <message>", "Sets the kick message.");
        st.put("im toggle <join/leave/ban/kick> <true/false>", "Toggles the different message types.");
        st.put("im setchannel <join/leave> <channel name>", "Toggles the join/leave messages.");
        st.put("Variables", "&guild&, &user&, &reason& (punishment)");
        return HelpHandler.helpCommand(st, "Message", e);
    }
}
