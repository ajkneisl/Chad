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

    private Map<IUser, Integer> COUNT;
    private Map<Future<?>, IUser> THREADS;

    @SuppressWarnings("InfiniteLoopStatement")
    private ThreadCountHandler()
    {
        this.COUNT = new HashMap<>();
        this.THREADS = new HashMap<>();
        JhoBot.EXECUTOR.execute(this::constant);
    }

    @SuppressWarnings("all")
    private void constant()
    {
        while (true)
        {
            if (!this.THREADS.isEmpty())
            {
                this.THREADS.forEach((k, v) -> {
                    if (k.isDone() == true)
                    {
                        this.COUNT.put(v, this.COUNT.get(v)-1);
                        this.THREADS.remove(k);
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
        else return this.COUNT.get(user) < 3;
    }

    public Map<IUser, Integer> getMap()
    {
        return this.COUNT;
    }

    public void addThread(Future<?> thread, IUser user)
    {
        this.COUNT.merge(user, 1, (a, b) -> a + b);
        this.THREADS.put(thread, user);
    }
}
