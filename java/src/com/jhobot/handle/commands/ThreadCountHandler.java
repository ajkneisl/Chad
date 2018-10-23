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
        new Thread(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                constant();
            }
        }, 0, 1000)).start();
    }

    @SuppressWarnings("all")
    private void constant()
    {
        if (!this.COUNT.isEmpty())
        {
            this.COUNT.forEach((k, v) -> {
                System.out.println("\n" + k.getName() + " " + v);
                if (v.size() != 0)
                {
                    for (Future<?> th : v)
                    {
                        if (th.isDone())
                        {
                            v.remove(th);
                            continue;
                        }
                    }
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
