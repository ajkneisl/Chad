package org.woahoverflow.chad.core.listener;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.obj.Guild;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.util.RequestBuffer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Discord Message Edit Event
 *
 * @author sho
 */
public final class MessageEditEvent {
    /**
     * Discord's Message Edit Event
     *
     * Mainly used to make sure edited messages don't contain swears
     *
     * @param event Message Edit Event
     */
    @EventSubscriber
    @SuppressWarnings("unused")
    public void messageEditEvent(sx.blah.discord.handle.impl.events.guild.channel.message.MessageEditEvent event) {
        Guild guild = GuildHandler.handle.getGuild(event.getGuild().getLongID());
        boolean stopSwear = (boolean) guild.getObject(Guild.DataType.SWEAR_FILTER);

        if (stopSwear) {
            // The arguments in the message
            String[] argArray = event.getMessage().getContent().split(" ");

            // Builds together the message & removes the special characters
            String character = String.join("", argArray);
            Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
            Matcher match = pt.matcher(character);

            while (match.find()) {
                character=character.replaceAll("\\" + match.group(), "");
            }

            // Checks if the word contains a swear word
            for (String swearWord : ChadVar.swearWords) {
                // Ass is a special case, due to words like `bass`
                if (swearWord.equalsIgnoreCase("ass") && character.contains("ass")) {
                    // Goes through all of the arguments
                    for (String argument : argArray) {
                        // If the argument is just ass
                        if (argument.equalsIgnoreCase("ass")) {
                            // Delete it
                            RequestBuffer.request(event.getMessage()::delete);
                            return;
                        }
                    }
                    continue;
                }

                // If it contains any other swear word, delete it
                if (character.toLowerCase().contains(swearWord)) {
                    RequestBuffer.request(event.getMessage()::delete);
                    return;
                }
            }
        }
    }
}
