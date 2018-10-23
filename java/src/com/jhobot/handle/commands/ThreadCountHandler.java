package com.jhobot.handle.commands;

import com.jhobot.core.JhoBot;
import org.checkerframework.checker.units.qual.C;
import sx.blah.discord.handle.obj.IUser;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ThreadCountHandler
{
    public static ThreadCountHandler HANDLER = new ThreadCountHandler();

    private Map<IUser, ArrayList<Future<?>>> COUNT;

    private ThreadCountHandler()
    {
        this.COUNT = new HashMap<>();
        Thread th = new Thread(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                constant();
            }
        }, 0, 1000));
        JhoBot.EXECUTOR.submit(th);
    }

    @SuppressWarnings("all")
    private void constant()
    {
        if (!this.COUNT.isEmpty())
        {
            this.COUNT.forEach((k, v) -> {
                if (v.size() != 0)
                {
                    for (int i = 0; v.size() > i; i++)
                    {
                        if (v.get(i).isDone())
                        {
                            v.remove(v.get(i));
                            continue;
                        }
                    }
                }
                else if (v.size() == 0)
                {
                    v.remove(k);
                }
            });

        }
    }

    public boolean allowThread(IUser user)
    {
        if (this.COUNT.get(user) == null)
        {
            return true;
        }
        else return this.COUNT.get(user).size() < 3;
    }

    public Map<IUser, ArrayList<Future<?>>> getMap()
    {
        return this.COUNT;
    }

    public void addThread(Future<?> thread, IUser user)
    {
        if (this.COUNT.containsKey(user))
        {
            ArrayList<Future<?>> th = this.COUNT.get(user);
            th.add(thread);
            this.COUNT.put(user, th);
        }
        else {
            ArrayList<Future<?>> th = new ArrayList<>();
            th.add(thread);
            this.COUNT.put(user, th);
        }
    }
}
