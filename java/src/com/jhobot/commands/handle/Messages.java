package com.jhobot.commands.handle;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
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
}
