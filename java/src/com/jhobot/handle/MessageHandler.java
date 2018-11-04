package com.jhobot.handle;

import com.jhobot.core.ChadVar;
import org.bson.Document;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

public class MessageHandler
{
    private final IChannel channel;
    public MessageHandler(IChannel ch)
    {
        this.channel = ch;
    }

    public void sendMessage(String message)
    {
        RequestBuffer.request(() ->{
           channel.sendMessage(message);
        });
    }

    public void sendEmbed(EmbedObject e)
    {
        RequestBuffer.request(() ->{
            channel.sendMessage(e);
        });
    }

    public void sendError(String error)
    {
        EmbedBuilder b = new EmbedBuilder();
        b.withTitle("Error");
        b.withDesc(error);
        b.withFooterText(Util.getTimeStamp());

        b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
        RequestBuffer.request(() -> {
           channel.sendMessage(b.build());
        });
    }

    public void send(String msg, String title)
    {
        EmbedBuilder b = new EmbedBuilder();
        b.withTitle(title);
        b.withDesc(msg);
        b.withFooterText(Util.getTimeStamp());
        b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
        RequestBuffer.request(() -> {
            channel.sendMessage(b.build());
        });
    }

    public void sendLog(EmbedObject e, DatabaseHandler databaseHandler, IGuild g)
    {
        if (!databaseHandler.getBoolean(g, "logging"))
            return;
        if (databaseHandler.getString(g, "logging_channel").equalsIgnoreCase("none"))
            return;

        try {
            RequestBuffer.request(() -> {
                g.getChannelByID(Long.parseLong(databaseHandler.getString(g, "logging_channel"))).sendMessage(e);
            });
        } catch (NumberFormatException ee)
        {
            ee.printStackTrace();
        }
    }

    public void sendPunishLog(String punishment, IUser punished, IUser punisher, IGuild g, List<String> reason)
    {
        Document doc = ChadVar.CACHE_DEVICE.getGuild(g).getDoc();
        if (!doc.getBoolean("logging"))
            return;
        if (doc.getString("logging_channel").equalsIgnoreCase("none"))
            return;

        StringBuilder sb = new StringBuilder();
        for (String s : reason)
        {
            sb.append(s).append(" ");
        }

        EmbedBuilder b = new EmbedBuilder().withTitle("Punishment : " + punished.getName()).appendField("Punished User", punished.getName(), true).appendField("Moderator", punisher.getName(), true).appendField("Punishment", punishment, true).appendField("Reason", sb.toString().trim(), false).withImage(punished.getAvatarURL()).withFooterText(Util.getTimeStamp());
        b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
        try {
            RequestBuffer.request(() -> {
                g.getChannelByID(Long.parseLong(doc.getString("logging_channel"))).sendMessage(b.build());
            });
        } catch (NumberFormatException ee)
        {
            ee.printStackTrace();
        }
    }

    public void sendConfigLog(String changedVal, String newval, String oldval, IUser mod, IGuild g)
    {
        Document doc = ChadVar.CACHE_DEVICE.getGuild(g).getDoc();
        if (!doc.getBoolean("logging"))
            return;
        if (doc.getString("logging_channel").equalsIgnoreCase("none"))
            return;

        EmbedBuilder b = new EmbedBuilder().withTitle("Config Change : " + changedVal).appendField("New Value", newval, true).appendField("Old Value", oldval, true).appendField("Admin", mod.getName(), true).withFooterText(Util.getTimeStamp());
        b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
        try {
            RequestBuffer.request(() -> {
                g.getChannelByID(Long.parseLong(doc.getString("logging_channel"))).sendMessage(b.build());
            });
        } catch (NumberFormatException ee)
        {
            ee.printStackTrace();
        }
    }

    public void sendFile(File file)
    {
        RequestBuffer.request(() -> {
            try {
                channel.sendFile(file);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        });
    }
}
