package org.woahoverflow.chad.core.listener;

import static org.woahoverflow.chad.core.listener.MessageRecieved.COMPILE;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.util.RequestBuffer;

public final class MessageEditEvent
{
    @EventSubscriber
    public void messageEditEvent(sx.blah.discord.handle.impl.events.guild.channel.message.MessageEditEvent e)
    {
        if (Chad.getGuild(e.getGuild()).getDocument().getBoolean("stop_swear"))
        {
            String[] argArray = e.getNewMessage().getContent().split(" ");

            // Gets the message from the cache :)
            String msg = Chad.getGuild(e.getGuild()).getDocument().getString("swear_message");
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
