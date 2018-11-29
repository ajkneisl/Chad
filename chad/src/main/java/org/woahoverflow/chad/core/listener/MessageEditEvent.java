package org.woahoverflow.chad.core.listener;

import static org.woahoverflow.chad.core.listener.MessageRecieved.COMPILE;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.CachingHandler;
import org.woahoverflow.chad.handle.MessageHandler;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.util.RequestBuffer;

public final class MessageEditEvent
{
    @EventSubscriber
    public void messageEditEvent(sx.blah.discord.handle.impl.events.guild.channel.message.MessageEditEvent e)
    {
        if (CachingHandler.getGuild(e.getGuild()).getDoc().getBoolean("stop_swear"))
        {
            String[] argArray = e.getNewMessage().getContent().split(" ");

            // Gets the message from the cache :)
            String msg = CachingHandler.getGuild(e.getGuild()).getDoc().getString("swear_message");
            msg = msg != null ? COMPILE.matcher(msg).replaceAll(e.getAuthor().getName()) : "No Swearing!";
            for (String s : argArray) {
                if (ChadVar.swearWords.contains(s.toLowerCase())) {
                    new MessageHandler(e.getChannel()).send(msg, "Swearing");
                    RequestBuffer.request(() -> e.getMessage().delete());
                }
            }
        }
    }
}
