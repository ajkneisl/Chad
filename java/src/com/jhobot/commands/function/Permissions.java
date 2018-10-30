package com.jhobot.commands.function;

import com.jhobot.core.ChadBot;
import com.jhobot.core.ChadVar;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.*;
import com.jhobot.handle.commands.permissions.PermissionHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;

public class Permissions implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            // j!perms role <role> add <perm>
            if (args.size() >= 3 && args.get(0).equalsIgnoreCase("role"))
            {
                args.remove(0); // removes so it can get role name
                StringBuilder b = new StringBuilder();
                List<IRole> r = new ArrayList<>();
                int i = 0;
                int i1 = 0;
                for (String s : args)
                {
                    i++;
                    b.append(s).append(" ");

                    r = RequestBuffer.request(() -> e.getGuild().getRolesByName(b.toString().trim())).get();
                    if (!r.isEmpty()) break;
                }

                if (args.size() == i)
                {
                    m.sendError("Invalid Role");
                    return;
                }
                IRole role = r.get(0);

                // isolates next arguments
                while (i > i1)
                {
                    i1++;
                    args.remove(0);
                }

                String nextArg = args.get(0);
                System.out.println(nextArg);
                args.remove(0); // isolates again
                switch (nextArg.toLowerCase()) {
                    default:
                        break;
                    case "add":
                        int add = ChadVar.PERMISSION_HANDLER.addCommandToRole(role, args.get(0));
                        if (add == 6) {
                            m.send("Added `" + args.get(0) + "` command to role `" + role.getName() + "`.", "Permissions");
                        } else {
                            m.sendError(ChadVar.PERMISSION_HANDLER.parseErrorCode(add));
                        }
                        break;
                    case "remove":
                        int rem = ChadVar.PERMISSION_HANDLER.removeCommandFromRole(role, args.get(0));
                        if (rem == 6) {
                            m.send("Removed `" + args.get(0) + "` command to role `" + role.getName() + "`.", "Permissions");
                        } else {
                            m.sendError(ChadVar.PERMISSION_HANDLER.parseErrorCode(rem));
                        }
                        break;
                    case "view":
                        System.out.println("view1");
                        if (ChadVar.DATABASE_HANDLER.getArray(e.getGuild(), role.getStringID()) == null || ChadVar.DATABASE_HANDLER.getArray(e.getGuild(), role.getStringID()).size() == 0) {
                            m.sendError("There's no permissions there!");
                            return;
                        }
                        System.out.println("view2");
                        EmbedBuilder b2 = new EmbedBuilder();
                        b2.withTitle("Viewing Permissions for `" + role.getName()+"`");
                        StringBuilder b3 = new StringBuilder();
                        ChadVar.DATABASE_HANDLER.getArray(e.getGuild(), role.getStringID()).forEach((v) -> b3.append(v).append(", "));
                        System.out.println(b3);
                        b2.withDesc(b3.toString());
                        m.sendEmbed(b2.build());
                        System.out.println("view3");
                        break;
                }
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return null;
    }
}

