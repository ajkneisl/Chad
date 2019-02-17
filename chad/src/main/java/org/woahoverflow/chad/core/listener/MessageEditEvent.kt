package org.woahoverflow.chad.core.listener

import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.util.RequestBuffer
import java.util.regex.Pattern

/**
 * Discord Message Edit Event
 *
 * @author sho
 */
class MessageEditEvent {
    /**
     * Discord's Message Edit Event
     *
     * Mainly used to make sure edited messages don't contain swears
     *
     * @param event Message Edit Event
     */
    @EventSubscriber
    fun messageEditEvent(event: sx.blah.discord.handle.impl.events.guild.channel.message.MessageEditEvent) {
        val guild = GuildHandler.handle.getGuild(event.guild.longID)
        val stopSwear = guild!!.getObject(Guild.DataType.SWEAR_FILTER) as Boolean

        if (stopSwear) {
            // The arguments in the message
            val argArray = event.message.content.split(" ")

            // Builds together the message & removes the special characters
            var character = argArray.joinToString("")
            val pt = Pattern.compile("[^a-zA-Z0-9]")
            val match = pt.matcher(character)

            while (match.find()) {
                character = character.replace(("\\" + match.group()).toRegex(), "")
            }

            // Checks if the word contains a swear word
            for (swearWord in ChadVar.swearWords) {
                // Ass is a special case, due to words like `bass`
                if (swearWord.equals("ass", ignoreCase = true) && character.contains("ass")) {
                    // Goes through all of the arguments
                    for (argument in argArray) {
                        // If the argument is just ass
                        if (argument.equals("ass", ignoreCase = true)) {
                            // Delete it
                            RequestBuffer.request { event.message.delete() }
                            return
                        }
                    }
                    continue
                }

                // If it contains any other swear word, delete it
                if (character.toLowerCase().contains(swearWord)) {
                    RequestBuffer.request { event.message.delete() }
                    return
                }
            }
        }
    }
}
