package org.woahoverflow.chad.commands.community;

import org.jetbrains.annotations.NotNull;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Why not?
 *
 * @author codebasepw
 */
public class Cuddle implements Command.Class {
    @NotNull
    @Override
    public Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            if (e.getMessage().getMentions().isEmpty()) {
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("You cuddled with yourself, how nice."));
                return;
            }

            IUser target = e.getMessage().getMentions().get(0);

            messageHandler.sendEmbed(new EmbedBuilder().withDesc("You cuddled with `" + target.getName() + "` without direct consent."));
        };
    }

    @NotNull
    @Override
    public Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("cuddle [@user]", "Cuddle with another user.");
        st.put("cuddle", "Cuddle with yourself. :)");
        return Command.helpCommand(st, "Cuddle", e);
    }
}
