package org.woahoverflow.chad.core.listener;

import java.security.SecureRandom;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.ui.UIHandler;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

import java.util.Timer;
import java.util.TimerTask;

public final class OnReady
{
    @SuppressWarnings("unused")
    @EventSubscriber
    public void onReadyEvent(ReadyEvent e)
    {
        e.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, "");

        //TODO: put this in its own thread class so rotationInteger can change the timings on it
        Chad.runThread(() -> {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!ChadVar.rotatePresence) {
                        return;
                    }
                    Object[] ar = ChadVar.presenceRotation.toArray();
                    int rotation = ar.length;
                    ChadVar.currentStatus = (String)ar[new SecureRandom().nextInt(rotation)];
                    e.getClient().changePresence(ChadVar.statusType, ActivityType.PLAYING, ChadVar.currentStatus);
                }
            }, 0, ChadVar.rotationInteger); // this cant be changed for some reason, i would probably have to reschedule the timer in order for this to work
        }, Chad.getInternalConsumer());

        // UI Begin
        UIHandler.handle
            .addLog("Bot started with " + e.getClient().getGuilds().size() + " guilds!", UIHandler.LogLevel.INFO);
        UIHandler.handle.update();
    }
}
