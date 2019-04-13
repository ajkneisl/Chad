package org.woahoverflow.chad.commands.punishments;

import org.jetbrains.annotations.NotNull;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.*;

public class Mute implements Command.Class
{
    public static List<Long> mutes = new ArrayList<>();

    @NotNull
    @Override
    public Runnable run(@NotNull MessageEvent e, @NotNull List<String> args)
    {
        return () ->
        {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            IChannel channel = e.getMessage().getChannel();

            EnumSet<Permissions> overrides = EnumSet.of(Permissions.SEND_MESSAGES);

            if (e.getMessage().getMentions().size() < 1)
            {
                if (!args.isEmpty())
                {
                    if (args.get(0).equalsIgnoreCase("list"))
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append("```\n");
                        sb.append("Muted users:\n");
                        for (long uid : mutes)
                        {
                            sb.append(e.getGuild().getUserByID(uid).mention() + "\n");
                        }
                        sb.append("```");
                    }
                }

                messageHandler.sendPresetError(MessageHandler.Messages.NO_MENTIONS);
                return;
            }

            for (IUser user : e.getMessage().getMentions())
            {
                if (mutes.contains(user.getLongID()))
                {
                    mutes.remove(user.getLongID());
                    channel.removePermissionsOverride(user);
                } else {
                    mutes.add(user.getLongID());
                    channel.overrideUserPermissions(user, null, overrides);
                }
            }

            messageHandler.sendMessage(String.format("Action completed on `%s` user(s).", e.getMessage().getMentions().size()));
        };
    }

    @NotNull
    @Override
    public Runnable help(@NotNull MessageEvent e)
    {
        HashMap<String, String> st = new HashMap<>();
        st.put("mute [@user]", "Mutes a user.");
        return Command.helpCommand(st, "Mute", e);
    }
}
