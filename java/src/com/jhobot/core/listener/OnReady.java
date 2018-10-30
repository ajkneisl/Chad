package com.jhobot.core.listener;

import com.jhobot.core.ChadBot;
import com.jhobot.core.ChadVar;
import com.jhobot.handle.ui.UIHandler;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

import java.util.*;


public class OnReady
{
    @EventSubscriber
    public void onReadyEvent(ReadyEvent e)
    {
        // automatic presence updater
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
                    e.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, (String)ar[new Random().nextInt(rotation)]);
                }
            }, 0, ChadVar.ROTATION_TIME); // this cant be changed for some reason, i would probably have to reschedule the timer in order for this to work
        });

        // UI Updater
        if (ChadVar.ALLOW_UI)
        {
            UIHandler h = new UIHandler(e.getClient());
            ChadVar.EXECUTOR_POOL.submit(() -> {
                java.util.Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        h.update();
                    }
                }, 0, 60000*5);
                h.getPanel().getRefreshButton().addActionListener((ActionEvent) ->  h.update());
            });
        }
    }
}