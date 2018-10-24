package com.jhobot.commands.function;

import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.PermissionLevels;
import com.jhobot.handle.commands.permissions.PermissionHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Permissions implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            // j!perms role <role> add <perm>
            if (args.size() >= 4 && args.get(0).equalsIgnoreCase("role"))
            {
                args.remove(0);
                StringBuilder sb = new StringBuilder();
                IRole r = null;
                int i2 = 0;
                for (String arg : args) {
                    i2++;
                    sb.append(arg).append(" ");
                    if (!e.getGuild().getRolesByName(sb.toString().trim()).isEmpty())
                        r = e.getGuild().getRolesByName(sb.toString().trim()).get(0);
                }

                for (int i = 0; i < i2; i++)
                {
                    args.remove(i);
                }
                if (r == null)
                {
                    m.sendError("Invalid Role!");
                    return;
                }

                if (args.get(0).equalsIgnoreCase("add"))
                {
                    args.remove(0);

                    HashMap<String, Boolean> h = new HashMap<>();
                    for (String arg : args)
                    {
                        h.put(arg, PermissionHandler.HANDLER.addCommandToRole(r, arg));
                    }

                    EmbedBuilder b = new EmbedBuilder();
                    b.withTitle("Permissions : " + r.getName());
                    h.forEach((k, v) -> {
                        String s = "";
                        if (v)
                            s = "Successfully added!";
                        else
                            s = "Invalid";
                        b.appendField(k, s, true);
                    });
                    m.sendEmbed(b.build());
                }
                m.sendError("Invalid Arguments!");
            }
            m.sendError("Invalid Arguments");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return null;
    }

    @Override
    public PermissionLevels level() {
        return PermissionLevels.MEMBER;
    }
}
