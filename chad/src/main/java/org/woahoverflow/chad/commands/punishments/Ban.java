package org.woahoverflow.chad.commands.punishments;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.PermissionUtils;

import java.util.*;

public class Ban implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());

            if (!e.getClient().getOurUser().getPermissionsForGuild(e.getGuild()).contains(Permissions.BAN))
            {
                m.sendError("The bot doesn't have permission for this!");
                return;
            }

            if (!e.getAuthor().getPermissionsForGuild(e.getGuild()).contains(Permissions.BAN))
            {
                m.sendError("You don't have permissions for this!");
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

            if (!PermissionUtils.hasHierarchicalPermissions(e.getChannel(), e.getClient().getOurUser(), user, Permissions.BAN))
            {
                m.sendError("Bot can't do this!");
                return;
            }

            if (!PermissionUtils.hasHierarchicalPermissions(e.getChannel(), e.getClient().getOurUser(), user, Permissions.BAN))
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

            if (ChadVar.DATABASE_HANDLER.getBoolean(e.getGuild(), "ban_msg_on"))
            {
                String msg = ChadVar.DATABASE_HANDLER.getString(e.getGuild(), "ban_message").replaceAll("&guild&", e.getGuild().getName()).replaceAll("&user&", user.getName()).replaceAll("&reason&", sb2.toString().trim());
                if (!user.isBot())
                    new MessageBuilder(e.getClient()).withChannel(e.getClient().getOrCreatePMChannel(user)).withContent(msg).build();
            }

            if (reason.isEmpty())
            {
                e.getGuild().banUser(user);
                reason.add("None");
                m.send("Successfully banned " + user.getName() + " for no reason.", "Banned User");
                m.sendPunishLog("Ban", user, e.getAuthor(), e.getGuild(), reason);
                return;
            }

            e.getGuild().banUser(user);
            m.send("Successfully banned " + user.getName() + " for " + sb2.toString().trim() + ".", "Banned User");
            m.sendPunishLog("Ban", user, e.getAuthor(), e.getGuild(), reason);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("ban <user>", "Bans a user with no reason.");
        st.put("ban <user> <reason>", "Bans a user with a specified reason.");
        return HelpHandler.helpCommand(st, "User Info", e);
    }
}
