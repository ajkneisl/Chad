package org.woahoverflow.chad.commands.info;

import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.Util;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GuildInfo implements Command.Class{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            IGuild g = e.getGuild();
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Guild : " + g.getName());
            b.appendField("Owner", g.getOwner().getName(), true);
            b.appendField("Role Amount", Integer.toString(g.getRoles().size()), true);
            int human = 0;
            int bot = 0;
            for (IUser u : g.getUsers())
            {
                if (u.isBot())
                    human++;
                else
                    bot++;
            }
            b.appendField("Humans", Integer.toString(human), true);
            b.appendField("Bots", Integer.toString(bot), true);
            b.appendField("User Amount", Integer.toString(g.getUsers().size()), true);
            b.appendField("Voice Channels", Integer.toString(g.getVoiceChannels().size()), true);
            b.appendField("Text Channels", Integer.toString(g.getChannels().size()), true);
            b.appendField("Categories", Integer.toString(g.getCategories().size()), true);
            Date date = Date.from(e.getGuild().getCreationDate());
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            b.appendField("Creation Date", format.format(date), false);

            b.withImage(e.getGuild().getIconURL());
            b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
            b.withFooterText(Util.getTimeStamp());
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("logging set <true/false>", "Toggles the logging functionality.");
        st.put("logging setchannel <channel name>", "Sets the logging channel.");
        return Command.helpCommand(st, "Message", e);
    }
}
