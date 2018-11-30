package org.woahoverflow.chad.commands.info;

import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GuildInfo implements Command.Class{
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            // Creates an embed builder and applies the title
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Guild : " + e.getGuild().getName());

            // Gets the human to bot.
            int human = 0;
            int bot = 0;
            for (IUser u : e.getGuild().getUsers())
            {
                if (!u.isBot()) {
                    human++;
                } else {
                    bot++;
                }
            }

            // Applies the description
            embedBuilder.withDesc(
                    "Owner `"+e.getGuild().getOwner().getName()+ '`' +
                            "\nRole Amount `"+e.getGuild().getRoles().size()+ '`' +
                              "\nHuman to Bots `"+human+ '/' +bot+ '`' +
                              "\nUser Amount `"+e.getGuild().getUsers().size()+ '`' +
                              "\nVoice Channels `"+e.getGuild().getVoiceChannels().size()+ '`' +
                              "\nText Channels `"+e.getGuild().getChannels().size()+ '`' +
                              "\nCategories `"+e.getGuild().getCategories().size()+ '`' +
                              "\nCreation Date `"+new SimpleDateFormat("MM/dd/yyyy").format(Date.from(e.getGuild().getCreationDate()))+ '`'
            );

            // Adds the guild's image and sends.
            embedBuilder.withImage(e.getGuild().getIconURL());
            new MessageHandler(e.getChannel()).sendEmbed(embedBuilder);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("guildinfo", "Gets information about the guild.");
        return Command.helpCommand(st, "Guild Info", e);
    }
}
