package org.woahoverflow.chad.commands.fun;

import java.net.MalformedURLException;
import java.security.SecureRandom;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.ui.UIHandler;
import org.woahoverflow.chad.framework.ui.UIHandler.LogLevel;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class PhotoEditor implements Command.Class {

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

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
                new MessageHandler(e.getChannel()).sendError("Invalid Format! \n Please use PNG or JPG");
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

            // Gets the image from the URL
            BufferedImage im;
            try {
                im = ImageIO.read(url);
            }  catch (IOException e1) {
                messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                return;
            }

            // Creates a image in "imgcache"
            File file = new File(System.getenv("appdata") + "\\chad\\imgcache\\img" + new SecureRandom().nextInt(2000) + ".png");

            // Makes sure the file doesn't exist already
            while (file.exists())
                file = new File(System.getenv("appdata") + "\\chad\\imgcache\\img" + new SecureRandom().nextInt(2000) + ".png");

            // Blur
            if (args.get(0).equalsIgnoreCase("blur"))
            {
                try {
                    // Blurs image
                    float[] matrix = new float[400];
                    for (int i = 0; i < 400; i++)
                        matrix[i] = 1.0f / 400.0f;
                    BufferedImageOp op = new ConvolveOp(new Kernel(20, 20, matrix), ConvolveOp.EDGE_NO_OP, null);
                    ImageIO.write(op.filter(im, null), "png", file);

                    // Sends the file
                    messageHandler.sendFile(file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                // Deletes the file
                if (!file.delete())
                    UIHandler.handle.addLog("Failed to delete file " + file.getPath(), LogLevel.SEVERE); // if the file didn't delete, send a log
                return;
            }

            // TODO: add more stuff to this, it's kinda lonely down here

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
