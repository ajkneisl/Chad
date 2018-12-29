package org.woahoverflow.chad.commands.info;

import java.util.HashMap;
import java.util.List;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author sho
 * @since 0.6.3 B2
 */
public class ChangeLog implements Command.Class
{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
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
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("changelog", "Gets the current change log");
        return Command.helpCommand(st, "Change Log", e);
    }
}
