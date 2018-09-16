package com.jhobot.handle;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

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
        RequestBuffer.request(() -> {
           channel.sendMessage(b.build());
        });
    }
}
