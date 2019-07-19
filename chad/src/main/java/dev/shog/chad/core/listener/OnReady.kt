package dev.shog.chad.core.listener

import dev.shog.chad.core.getLogger
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent

/**
 * The on ready event from Discord
 *
 * @author sho
 * @since 0.6.3 B2
 */
class OnReady {
    /**
     * Discord Ready Event
     *
     * @param event Discord Ready Event
     */
    @EventSubscriber
    fun onReadyEvent(event: ReadyEvent) { getLogger().info("Bot started with {} guilds!", event.client.guilds.size) }
}
