package org.woahoverflow.chad.core.listener;

import java.security.SecureRandom;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.ui.UIHandler;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

import java.util.Timer;
import java.util.TimerTask;


public class OnReady
{
    @SuppressWarnings("unused")
    @EventSubscriber
    public final void onReadyEvent(ReadyEvent e)
    {
        e.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, "");

        //TODO: put this in its own thread class so i can change the timings on it
        ChadVar.EXECUTOR_POOL.submit(() -> {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!ChadVar.ROTATE_PRESENCE)
                        return;
                    Object[] ar = ChadVar.PRESENCE_ROTATION.toArray();
                    int rotation = ar.length;
                    ChadVar.CURRENT_STATUS = (String)ar[new SecureRandom().nextInt(rotation)];
                    e.getClient().changePresence(ChadVar.STATUS_TYPE, ActivityType.PLAYING, ChadVar.CURRENT_STATUS);
                }
            }, 0, ChadVar.ROTATION_TIME); // this cant be changed for some reason, i would probably have to reschedule the timer in order for this to work
        });

        // UI Begin
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        ChadVar.UI_DEVICE.addLog("Bot started with " + e.getClient().getGuilds().size() + " guilds!", UIHandler.LogLevel.INFO);
        ChadVar.UI_DEVICE.update();
    }
}
