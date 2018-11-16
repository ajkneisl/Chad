package org.woahoverflow.chad.commands.nsfw;

import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

import java.util.HashMap;
import java.util.List;

public class NSFW implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler h = new MessageHandler(e.getChannel());
            if (e.getAuthor().getPermissionsForGuild(e.getGuild()).contains(Permissions.ADMINISTRATOR))
            {
                h.sendError("You don't have permission for this!");
                return;
            }
            if (e.getChannel().isNSFW())
            {
                h.send("Removed NSFW status from this channel!", "NSFW");
                e.getChannel().changeNSFW(false);
            }
            else {
                h.send("Added NSFW status to this channel!", "NSFW");
                e.getChannel().changeNSFW(true);
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("NSFW", "Toggles NSFW status for the channel");
        return HelpHandler.helpCommand(st, "NSFW", e);
    }
}
