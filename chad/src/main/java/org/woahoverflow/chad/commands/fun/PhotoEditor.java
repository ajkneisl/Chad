package org.woahoverflow.chad.commands.fun;

import org.jetbrains.annotations.NotNull;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Guild;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Use preset options to modify a photo
 *
 * @author sho, codebasepw
 */
public class PhotoEditor implements Command.Class {
    @Override
    public final Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());
            String prefix = (String) GuildHandler.getGuild(e.getGuild().getLongID()).getObject(Guild.DataType.PREFIX);

            // Makes sure the user has attached a file
            if (e.getMessage().getAttachments().isEmpty()) {
                messageHandler.sendError("No file was found!");
                return;
            }

            // Makes sure they added arguments
            if (args.isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "pe **deepfry**");
                return;
            }

            // Makes sure the attachment is a PNG or JPG
            if (!(e.getMessage().getAttachments().get(0).getUrl().endsWith(".png") || e.getMessage().getAttachments().get(0).getUrl().endsWith(".jpg"))) {
                new MessageHandler(e.getChannel(), e.getAuthor()).sendError("Invalid Format!\nPlease use PNG or JPG");
                return;
            }

            // Assigns the URL to the attachment's URL
            URL url;
            try {
                url = new URL(e.getMessage().getAttachments().get(0).getUrl());
            } catch (MalformedURLException e1) {
                messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION);
                return;
            }


            // Deepfry
            if (args.get(0).equalsIgnoreCase("deepfry")) {
                messageHandler.sendEmbed(new EmbedBuilder().withImage(
                        Objects.requireNonNull(JsonHandler.INSTANCE.read("https://nekobot.xyz/api/imagegen?type=deepfry&image=" + url)).getString("message"))
                );
                return;
            }

            // If none of the arguments were met, return;
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "pe **deepfry**");
        };
    }

    @Override
    public final Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("pe deepfry <image>", "Deepfries an image.");
        return Command.helpCommand(st, "Photo Editor", e);
    }
}
