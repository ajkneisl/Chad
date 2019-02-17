package org.woahoverflow.chad.commands.info;

import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * The recent changes
 *
 * @author sho
 */
public class ChangeLog implements Command.Class {
    @Override
    public Runnable run(MessageEvent e, List<String> args) {
        return () -> {
            EmbedBuilder embedBuilder = new EmbedBuilder();

            // Builds it (recent as in the most recent version(s))
            embedBuilder.withTitle("**Change Log** : Recent");

            String changeLogBuilder =
                "`adjusted help command` : The help command now only shows what commands you have permission to, and the format has changed!\n\n"
                    + "`music` : Chad now has music! To try it out, type `j!play yt <song name>`!\n\n"
                    + "`player profiles` : Chad now has player profiles! Check this out with `j!profile`!\n\n"
                    + "`balance` : Your balance is now global though-out all guilds with Chad!\n\n"
                    + "`deepfry` : DeepFry your images with `j!pe deepfry`!\n\n"
                    + "`aliases` : Gives all aliases for a command!\n\n"
                    + "`dog stuff` : There's now dogfact & doggallery!\n\n"
                    + "Find an issue? Please report it to us at https://woahoverflow.org";

            embedBuilder.withDesc(changeLogBuilder);

            // Sends the message
            new MessageHandler(e.getChannel(), e.getAuthor()).sendEmbed(embedBuilder);
        };
    }

    @Override
    public Runnable help(MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("changelog", "Gets the current change log");
        return Command.helpCommand(st, "Change Log", e);
    }
}
