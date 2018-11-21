package org.woahoverflow.chad.commands.function;

import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

import java.util.HashMap;
import java.util.List;

public class NSFW implements Command.Class  {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler h = new MessageHandler(e.getChannel());
            if (!e.getAuthor().getPermissionsForGuild(e.getGuild()).contains(Permissions.ADMINISTRATOR))
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
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("nsfw", "Toggles NSFW status for the channel");
        return Command.helpCommand(st, "NSFW", e);
    }
}
