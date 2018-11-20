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
            Date date = Date.from(e.getGuild().getCreationDate());
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            int human = 0;
            int bot = 0;
            for (IUser u : g.getUsers())
            {
                if (!u.isBot())
                    human++;
                else
                    bot++;
            }
            b.withDesc(
                    "Owner `"+g.getOwner().getName()+"`" +
                            "\nRole Amount `"+g.getRoles().size()+"`"+
                              "\nHuman to Bots `"+human+"/"+bot+"`" +
                              "\nUser Amount `"+g.getUsers().size()+"`" +
                              "\nVoice Channels `"+g.getVoiceChannels().size()+"`" +
                              "\nText Channels `"+g.getChannels().size()+"`" +
                              "\nCategories `"+g.getCategories().size()+"`" +
                              "\nCreation Date `"+format.format(date)+"`"
            );
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
