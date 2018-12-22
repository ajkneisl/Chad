package org.woahoverflow.chad.commands.fun;

import java.util.HashMap;
import java.util.List;

import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author codebasepw
 * @since 0.7.0
 */
public class Cuddle implements Command.Class {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            if (e.getMessage().getMentions().isEmpty())
            {
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("You cuddled with yourself, how nice."));
                return;
            }

            IUser target = e.getMessage().getMentions().get(0);

            messageHandler.sendEmbed(new EmbedBuilder().withDesc("You cuddled with " + target.getName() + ", they most likely didn't consent, how nice."));
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("cuddle <@user>", "Cuddle with another user.");
        st.put("cuddle", "Cuddle with yourself :)");
        return Command.helpCommand(st, "Cuddle", e);
    }
}
