package org.woahoverflow.chad.handle;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.woahoverflow.chad.core.ChadVar;
import sx.blah.discord.handle.obj.IUser;

import java.util.concurrent.Future;

public class ThreadCountHandler
{
    private final ConcurrentHashMap<IUser, ArrayList<Future<?>>> threadCount;

    public ThreadCountHandler()
    {
        threadCount = new ConcurrentHashMap<>();
        Thread th = new Thread(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                constant();
            }
        }, 0, 1000));
        ChadVar.EXECUTOR_POOL.submit(th);
    }

    private void constant()
    {
        if (!threadCount.isEmpty())
        {
            threadCount.forEach((key, val) -> {
                if (!val.isEmpty())
                {
                    for (int i = 0; val.size() > i; i++)
                    {
                        if (val.get(i).isDone())
                        {
                            val.remove(val.get(i));
                        }
                    }
                }
                else {
                    threadCount.remove(key);
                }
            });

        }
    }

    public final boolean canRun(IUser user)
    {
        return threadCount.get(user) == null || threadCount.get(user).size() < 3;
    }

    public final Map<IUser, ArrayList<Future<?>>> getMap()
    {
        return threadCount;
    }

    public final void addThread(Future<?> thread, IUser user)
    {
        if (threadCount.containsKey(user))
        {
            ArrayList<Future<?>> th = threadCount.get(user);
            th.add(thread);
            threadCount.put(user, th);
        }
        else {
            ArrayList<Future<?>> th = new ArrayList<>();
            th.add(thread);
            threadCount.put(user, th);
        }
    }
}
