package com.jhobot.commands.fun;

import com.jhobot.JhoBot;
import com.jhobot.handle.DB;
import com.jhobot.handle.Messages;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PhotoEditor implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            Messages m = new Messages(e.getChannel());
            if (e.getMessage().getAttachments().isEmpty())
            {
                m.sendError("No file was found!");
            }

            if (args.size() == 0)
            {
                m.sendError("Invalid Arguments!");
                return;
            }

            System.setProperty("http.agent", "Chrome");
            BufferedImage im = null;
            try {
                im = ImageIO.read(new URL(e.getMessage().getAttachments().get(0).getUrl()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            File f = new File(System.getenv("appdata") + "\\jho\\imgcache\\img" + new java.util.Random().nextInt(2000) + ".png");

            if (args.get(0).equalsIgnoreCase("blur"))
            {
                try {
                    float[] matrix = new float[400];
                    for (int i = 0; i < 400; i++)
                        matrix[i] = 1.0f/400.0f;
                    BufferedImageOp op = new ConvolveOp(new Kernel(20, 20, matrix), ConvolveOp.EDGE_NO_OP, null );

                    BufferedImage i = null;

                    ImageIO.write(op.filter(im, i), "png", f);

                    m.sendFile(f);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                if (f.delete())
                    System.err.println("Failed to delete file " + f.getPath());
                return;
            }

            help(e, args);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return () -> {
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Help : Random");
            b.appendField(JhoBot.db.getString(e.getGuild(), "prefix") + "pe blur [photo]", "Blurs a photo.", false);
            b.withFooterText(Util.getTimeStamp());
            b.withColor(new Color(new java.util.Random().nextFloat(), new java.util.Random().nextFloat(), new java.util.Random().nextFloat()));
            new Messages(e.getChannel()).sendEmbed(b.build());
        };
    }
}
