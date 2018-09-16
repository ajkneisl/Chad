package com.jhobot.handle;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.util.Random;

public class Messages
{
    private IChannel channel;
    public Messages(IChannel ch)
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
}
