package org.woahoverflow.chad.commands.fun;

import java.net.MalformedURLException;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import sx.blah.discord.util.EmbedBuilder;

/**
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class PhotoEditor implements Command.Class {

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Makes sure the user has attached a file
            if (e.getMessage().getAttachments().isEmpty())
            {
                messageHandler.sendError("No file was found!");
                return;
            }

            // Makes sure they added arguments
            if (args.isEmpty())
            {
                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }

            // Makes sure the attachment is a PNG or JPG
            if (!(e.getMessage().getAttachments().get(0).getUrl().endsWith(".png") || e.getMessage().getAttachments().get(0).getUrl().endsWith(".jpg")))
            {
                new MessageHandler(e.getChannel(), e.getAuthor()).sendError("Invalid Format! \n Please use PNG or JPG");
                return;
            }

            // Assigns the URL to the attachment's URL
            URL url;
            try {
                url = new URL(e.getMessage().getAttachments().get(0).getUrl());
            } catch (MalformedURLException e1) {
                messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                return;
            }


            // Deepfry
            if (args.get(0).equalsIgnoreCase("deepfry"))
            {
                messageHandler.sendEmbed(new EmbedBuilder().withImage(
                    JsonHandler.handle.read("https://nekobot.xyz/api/imagegen?type=deepfry&image=" + url).getString("message"))
                );
                return;
            }

            // If none of the arguments were met, return;
            messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("pe blur <image>", "Blurs a photo.");
        return Command.helpCommand(st, "Photo Editor", e);
    }
}
