package org.woahoverflow.chad.core.listener;

import static org.woahoverflow.chad.core.listener.MessageReceived.COMPILE;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Guild;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.util.RequestBuffer;

/**
 * Discord Message Edit Event
 *
 * @author sho
 * @since 0.6.3 B2
 */
public final class MessageEditEvent
{

    /**
     * Discord's Message Edit Event
     *
     * Mainly used to make sure edited messages don't contain swears
     *
     * @param event Message Edit Event
     */
    @EventSubscriber
    @SuppressWarnings("unused")
    public void messageEditEvent(sx.blah.discord.handle.impl.events.guild.channel.message.MessageEditEvent event)
    {
        Guild guild = GuildHandler.handle.getGuild(event.getGuild().getLongID());
        boolean stopSwear = (boolean) guild.getObject(Guild.DataType.SWEAR_FILTER);

        if (stopSwear)
        {
            String[] argArray = event.getNewMessage().getContent().split(" ");

            // Gets the message from the cache :)
            String msg = (String) guild.getObject(Guild.DataType.SWEAR_FILTER_MESSAGE);
            msg = msg != null ? COMPILE.matcher(msg).replaceAll(event.getAuthor().getName()) : "No Swearing!";
            for (String s : argArray) {
                if (ChadVar.swearWords.contains(s.toLowerCase())) {
                    new MessageHandler(event.getChannel(), event.getAuthor()).send(msg, "Swearing");
                    RequestBuffer.request(() -> event.getMessage().delete());
                }
            }
        }
    }
}
