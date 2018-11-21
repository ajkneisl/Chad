package org.woahoverflow.chad.commands.function;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.HashMap;
import java.util.List;

public class Logging implements Command.Class  {
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

            if (args.size() == 2 && args.get(0).equalsIgnoreCase("set"))
            {
                String bool;

                if (args.get(1).equalsIgnoreCase("on"))
                    bool = "On";
                else if (args.get(1).equalsIgnoreCase("off"))
                    bool = "Off";
                else
                {
                    m.sendError("You didn't input on or off!");
                    return;
                }

                boolean abool = false;
                if (bool.equalsIgnoreCase("on"))
                    abool = true;
                ChadVar.DATABASE_DEVICE.set(e.getGuild(), "logging", abool);
                m.sendConfigLog("Logging", bool, Boolean.toString(ChadVar.DATABASE_DEVICE.getBoolean(e.getGuild(), "logging")), e.getAuthor(), e.getGuild());
                m.send("Changed logging to " + bool, "Changed Logging");

                return;
            }

            if (args.size() >= 2 && args.get(0).equalsIgnoreCase("setchannel"))
            {
                args.remove(0);

                StringBuilder b = new StringBuilder();
                for (String s : args)
                {
                    b.append(s).append(" ");
                }

                if (e.getGuild().getChannelsByName(b.toString().trim()).isEmpty())
                {
                    new MessageHandler(e.getChannel()).sendError("Invalid Channel");
                    return;
                }

                IChannel ch = e.getGuild().getChannelsByName(b.toString().trim()).get(0);

                if (ch == null)
                {
                    m.sendError("Invalid Channel");
                    return;
                }

                if (ChadVar.DATABASE_DEVICE.getString(e.getGuild(), "logging_channel").equalsIgnoreCase("none"))
                {
                    m.sendConfigLog("Logging Channel", b.toString().trim(), "none", e.getAuthor(), e.getGuild());
                }
                else {
                    m.sendConfigLog("Logging Channel", b.toString().trim(), e.getGuild().getChannelByID(Long.parseLong(ChadVar.DATABASE_DEVICE.getString(e.getGuild(), "logging_channel"))).getName(), e.getAuthor(), e.getGuild());
                }
                m.send("Changed logging channel to " + b.toString().trim(), "Changed Logging Channel");
                ChadVar.DATABASE_DEVICE.set(e.getGuild(), "logging_channel", ch.getStringID());
                return;
            }

            m.sendError("Invalid Arguments");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("logging set <on/off>", "Toggles the logging functionality.");
        st.put("logging setchannel <channel name>", "Sets the logging channel.");
        return Command.helpCommand(st, "Logging", e);
    }
}
