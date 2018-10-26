package com.jhobot.commands.punishments;

import com.jhobot.core.ChadBot;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.PermissionUtils;

import java.util.*;
import java.util.List;

public class Kick implements Command {
    @DefineCommand(category = Category.PUNISHMENTS)
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            if (!e.getClient().getOurUser().getPermissionsForGuild(e.getGuild()).contains(Permissions.KICK))
            {
                m.sendError("The bot doesn't have permissions for this!");
                return;
            }
            IUser user = null;
            List<String> reason = new ArrayList<>();
            if (!e.getMessage().getMentions().isEmpty() && args.get(0).equalsIgnoreCase(e.getMessage().getMentions().get(0).mention()))
            {
                user = e.getMessage().getMentions().get(0);
                args.remove(0);
                reason = args;
            }
            else {
                StringBuilder sb = new StringBuilder();
                for (String s : args)
                {
                    if (user == null)
                    {
                        sb.append(s).append(" ");
                        if (!e.getGuild().getUsersByName(sb.toString().trim()).isEmpty())
                        {
                            user = e.getGuild().getUsersByName(sb.toString().trim()).get(0);
                        }
                    }
                    else {
                        reason.add(s);
                    }
                }
            }

            if (user == null)
            {
                m.sendError("Invalid User!");
                return;
            }

            if (user.getPermissionsForGuild(e.getGuild()).contains(Permissions.ADMINISTRATOR))
            {
                m.sendError("Bot can't do this!");
                return;
            }
            
            
            if (!PermissionUtils.hasHierarchicalPermissions(e.getChannel(), e.getClient().getOurUser(), user, Permissions.KICK))
            {
                m.sendError("Bot can't do this!");
                return;
            }

            if (!PermissionUtils.hasHierarchicalPermissions(e.getChannel(), e.getClient().getOurUser(), user, Permissions.KICK))
            {
                m.sendError("You can't do this!");
                return;
            }

            StringBuilder sb2 = new StringBuilder();
            if (reason.size() != 0)
            {
                for (String s : reason)
                {
                    sb2.append(s).append(" ");
                }
            }
            else {
                sb2.append("no reason");
            }

            if (ChadBot.DATABASE_HANDLER.getBoolean(e.getGuild(), "kick_msg_on"))
            {
                String msg = ChadBot.DATABASE_HANDLER.getString(e.getGuild(), "kick_message").replaceAll("&guild&", e.getGuild().getName()).replaceAll("&user&", user.getName()).replaceAll("&reason&", sb2.toString().trim());
                if (!user.isBot())
                    new MessageBuilder(e.getClient()).withChannel(e.getClient().getOrCreatePMChannel(user)).withContent(msg).build();
            }

            if (reason.isEmpty())
            {
                e.getGuild().kickUser(user);
                reason.add("None");
                m.send("Successfully kicked " + user.getName() + " for no reason.", "Kicked User");
                m.sendPunishLog("Kick", user, e.getAuthor(), ChadBot.DATABASE_HANDLER, e.getGuild(), reason);
                return;
            }

            e.getGuild().kickUser(user);
            m.send("Successfully kicked " + user.getName() + " for " + sb2.toString().trim() + ".", "Kicked User");
            m.sendPunishLog("Kick", user, e.getAuthor(), ChadBot.DATABASE_HANDLER, e.getGuild(), reason);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("kick <user>", "Kicks a user with no reason.");
        st.put("kick <user> <reason>", "Kicks a user with a specified reason.");
        return HelpHandler.helpCommand(st, "User Info", e);
    }
}
