package org.woahoverflow.chad.core.listener;

import org.woahoverflow.chad.core.ChadInstance;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.handle.ArgumentHandler;
import org.woahoverflow.chad.framework.handle.Reddit;
import org.woahoverflow.chad.framework.sync.WebsiteSync;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.util.RequestBuffer;

import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;

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
        // UI Begin
        if (ArgumentHandler.isToggled("disable_ui")) {
            ChadInstance.getLogger().info("Bot started with {} guilds!", event.getClient().getGuilds().size());
        }
        else {
            ChadInstance.getLogger().debug("Bot started with " + event.getClient().getGuilds().size() + " guilds!");
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                WebsiteSync.sync(event.getClient());
            }
        }, 0, 1000 * 60 * 5);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Reddit.getSubreddits().clear();

                ChadInstance.getLogger().debug("Reset all cached subreddits!");
            }
        }, 0, 86400 * 1000);

        // If the presence rotation is disabled, return
        if (!ChadVar.rotatePresence)
            return;

        // Rotation Values
        Object[] ar = ChadVar.presenceRotation.toArray();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!ChadVar.rotatePresence) return;

                // Sets the new status
                if (ar.length == 0)
                    ChadVar.currentStatus = "uh oh!";
                else
                    ChadVar.currentStatus = (String) ar[new SecureRandom().nextInt(ar.length)];

                // Changes the discord presence
                RequestBuffer.request(() -> event.getClient().changePresence(ChadVar.statusType, ActivityType.PLAYING, ChadVar.currentStatus));
            }
        }, 0, 1000 * 60 * 5);
    }
}
