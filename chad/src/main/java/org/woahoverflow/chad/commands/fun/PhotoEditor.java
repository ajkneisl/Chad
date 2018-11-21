package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
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

public class PhotoEditor implements Command.Class {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            if (e.getMessage().getAttachments().isEmpty())
            {
                m.sendError("No file was found!");
            }

            if (args.size() == 0)
            {
                m.sendError("Invalid Arguments!");
                return;
            }

            if (!(e.getMessage().getAttachments().get(0).getUrl().endsWith(".png") || e.getMessage().getAttachments().get(0).getUrl().endsWith(".jpg")))
            {
                new MessageHandler(e.getChannel()).sendError("Invalid Format! \n Please use PNG or JPG");
                return;
            }
            System.setProperty("http.agent", "Chrome");
            BufferedImage im = null;
            try {
                im = ImageIO.read(new URL(e.getMessage().getAttachments().get(0).getUrl()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            File f = new File(System.getenv("appdata") + "\\chad\\imgcache\\img" + new java.util.Random().nextInt(2000) + ".png");
            
            while (f.exists())
            {
                f = new File(System.getenv("appdata") + "\\chad\\imgcache\\img" + new java.util.Random().nextInt(2000) + ".png");
            }

            if ("blur".equals(args.get(0).toLowerCase()))
            {
                try {
                    float[] matrix = new float[400];
                    for (int i = 0; i < 400; i++)
                        matrix[i] = 1.0f / 400.0f;
                    BufferedImageOp op = new ConvolveOp(new Kernel(20, 20, matrix), ConvolveOp.EDGE_NO_OP, null);

                    BufferedImage i = null;

                    ImageIO.write(op.filter(im, null), "png", f);

                    m.sendFile(f);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                if (f.delete())
                    System.err.println("Failed to delete file " + f.getPath());
                return;
            }

            new MessageHandler(e.getChannel()).sendError("Invalid Arguments");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("pe blur <image>", "Blurs a photo.");
        return Command.helpCommand(st, "Photo Editor", e);
    }
}
