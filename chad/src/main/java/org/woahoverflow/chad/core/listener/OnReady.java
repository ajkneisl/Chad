package org.woahoverflow.chad.core.listener;

import org.woahoverflow.chad.core.ChadInstance;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;

/**
 * The on ready event from Discord
 *
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public final class OnReady {
    /**
     * Discord Ready Event
     *
     * @param event Discord Ready Event
     */
    @EventSubscriber
    @SuppressWarnings("unused")
    public void onReadyEvent(ReadyEvent event) {
        ChadInstance.getLogger().info("Bot started with {} guilds!", event.getClient().getGuilds().size());
    }
}
