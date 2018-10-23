package com.jhobot.handle.commands;

import com.jhobot.core.JhoBot;
import org.checkerframework.checker.units.qual.C;
import sx.blah.discord.handle.obj.IUser;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ThreadCountHandler
{
    public static ThreadCountHandler HANDLER = new ThreadCountHandler();

    private Map<IUser, ArrayList<Thread>> COUNT;

    private ThreadCountHandler()
    {
        this.COUNT = new HashMap<>();
        JhoBot.EXECUTOR.execute(this::constant);
    }

    @SuppressWarnings("all")
    private void constant()
    {
        while (true)
        {
            if (!this.COUNT.isEmpty())
            {
                this.COUNT.forEach((k, v) -> {
                    for (Thread th : v)
                    {
                        if (!th.isAlive())
                        {
                            v.remove(th);
                        }
                    }
                });
            }
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

    public Map<IUser, ArrayList<Thread>> getMap()
    {
        return this.COUNT;
    }

    public void addThread(Thread thread, IUser user)
    {
        if (this.COUNT.containsKey(user))
        {
            ArrayList<Thread> th = this.COUNT.get(user);
            th.add(thread);
            this.COUNT.put(user, th);
        }
        else {
            ArrayList<Thread> th = new ArrayList<>();
            th.add(thread);
            this.COUNT.put(user, th);
        }
    }
}
