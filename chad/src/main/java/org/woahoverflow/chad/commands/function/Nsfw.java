package org.woahoverflow.chad.commands.function;

import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

public class Nsfw implements Command.Class  {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // Makes sure they've got permissions
            if (!RequestBuffer.request(() -> e.getChannel().getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.MANAGE_CHANNEL)).get())
            {
                messageHandler.sendError(MessageHandler.BOT_NO_PERMISSION);
                return;
            }

            // If the channel is NSFW, revoke, if not, add
            if (RequestBuffer.request(() -> e.getChannel().isNSFW()).get())
            {
                messageHandler.send("Removed NSFW status from this channel!", "Nsfw");
                RequestBuffer.request(() -> e.getChannel().changeNSFW(false));
            }
            else {
                messageHandler.send("Added NSFW status to this channel!", "Nsfw");
                RequestBuffer.request(() -> e.getChannel().changeNSFW(true));
            }
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("nsfw", "Toggles NSFW status for the channel");
        return Command.helpCommand(st, "Nsfw", e);
    }
}
