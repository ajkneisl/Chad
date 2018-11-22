package org.woahoverflow.chad.commands.function;

import org.bson.Document;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class Permissions implements Command.Class  {
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
                IRole role = null;
                int i = 0;
                int i1 = 0;
                for (String s : args)
                {
                    i++;
                    b.append(s).append(" ");

                    r = RequestBuffer.request(() -> e.getGuild().getRoles()).get();
                    for (IRole rol : r)
                    {
                        if (rol.getName().equalsIgnoreCase(b.toString().trim()))
                        {
                            role = rol;
                            break;
                        }
                    }
                    if (role != null) break;
                }

                if (args.size() == i)
                {
                    m.sendError("Invalid Role!");
                    return;
                }

                // isolates next arguments
                while (i > i1)
                {
                    i1++;
                    args.remove(0);
                }

                String nextArg = args.get(0);
                args.remove(0); // isolates again
                switch (nextArg.toLowerCase()) {
                    case "add":
                        if (!(args.size() >= 1))
                        {
                            m.sendError("Invalid Arguments");
                            return;
                        }
                        int add = ChadVar.PERMISSION_DEVICE.addCommandToRole(role, args.get(0));
                        if (add == 6) {
                            m.send("Added `" + args.get(0) + "` command to role `" + role.getName() + "`.", "Permissions");
                        } else {
                            m.sendError(ChadVar.PERMISSION_DEVICE.parseErrorCode(add));
                        }
                        return;
                    case "remove":
                        if (!(args.size() >= 1))
                        {
                            m.sendError("Invalid Arguments");
                            return;
                        }
                        int rem = ChadVar.PERMISSION_DEVICE.removeCommandFromRole(role, args.get(0));
                        if (rem == 6) {
                            m.send("Removed `" + args.get(0) + "` command to role `" + role.getName() + "`.", "Permissions");
                        } else {
                            m.sendError(ChadVar.PERMISSION_DEVICE.parseErrorCode(rem));
                        }
                        return;
                    case "view":
                        Document doc = ChadVar.CACHE_DEVICE.getGuild(e.getGuild()).getDoc();
                        ArrayList<String> ar = (ArrayList<String>) doc.get(role.getStringID());
                        if (ar == null || ar.size() == 0) {
                            m.sendError("There's no permissions in this role!");
                            return;
                        }
                        EmbedBuilder b2 = new EmbedBuilder();
                        b2.withTitle("Viewing Permissions for `" + role.getName()+"`");
                        StringBuilder b3 = new StringBuilder();
                        ar.forEach((v) -> b3.append(", ").append(v));
                        b2.withDesc(b3.toString().trim().replaceFirst(",", ""));
                        m.sendEmbed(b2.build());
                        return;
                    default:
                        m.sendError("Invalid Arguments");
                        return;
                }
            }
            m.sendError("Invalid Arguments");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("perm role <role name> add <command>", "Adds a Chad command to a Discord role.");
        st.put("perm role <role name> remove <command>", "Removes a Chad command to a Discord role.");
        st.put("perm role <role name> view", "Displays all Chad commands tied to that Discord role.");
        return Command.helpCommand(st, "Permissions", e);
    }
}

