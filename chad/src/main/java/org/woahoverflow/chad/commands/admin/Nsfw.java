package org.woahoverflow.chad.commands.admin;

import org.jetbrains.annotations.NotNull;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;

/**
 * Toggle NSFW status within a channel
 *
 * @author sho
 */
public class Nsfw implements Command.Class  {
    @Override
    public final Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Makes sure they've got permissions
            if (!RequestBuffer.request(() -> e.getChannel().getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.MANAGE_CHANNEL)).get()) {
                messageHandler.sendPresetError(MessageHandler.Messages.BOT_NO_PERMISSION);
                return;
            }

            // If the channel is NSFW, revoke, if not, add
            if (RequestBuffer.request(() -> e.getChannel().isNSFW()).get()) {
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("Removed NSFW status from this channel!"));
                RequestBuffer.request(() -> e.getChannel().changeNSFW(false));
            }
            else {
                messageHandler.sendEmbed(new EmbedBuilder().withDesc("Added NSFW status from this channel!"));
                RequestBuffer.request(() -> e.getChannel().changeNSFW(true));
            }
        };
    }

    @Override
    public final Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("nsfw", "Toggles NSFW status for the channel.");
        return Command.helpCommand(st, "NSFW", e);
    }
}
