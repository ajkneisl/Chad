package org.woahoverflow.chad.framework.handle

import org.woahoverflow.chad.core.ChadInstance
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Thread management file.
 *
 * Each user run command is a runnable, and that runnable is ran here and is tied to that user's Long ID.
 *
 * @author sho
 */
object ThreadHandler {
    @JvmStatic
    var runningThreads = 0

    @JvmStatic
    val threadHash = ConcurrentHashMap<Long, ArrayList<Future<*>>>()

    @JvmStatic
    private val executorService = Executors.newCachedThreadPool()

    /**
     * Runs a thread for a user.
     *
     * @param thread The thread to run
     * @param long The user's ID
     */
    @JvmStatic
    fun runThread(thread: Runnable, long: Long) {
        val future: Future<*> = executorService.submit(thread)

        if (threadHash.containsKey(long)) {
            val th = threadHash[long]!!
            th.add(future)
            threadHash[long] = th
        } else {
            val th = arrayListOf(future)
            threadHash[long] = th
        }

        runningThreads++
    }

    /**
     * Checks if a user can run a thread.
     *
     * A user can't run a thread if they've got more than 3 threads currently running.
     *
     * @param long The user's ID
     */
    @JvmStatic
    fun canUserRunThread(long: Long): Boolean
    {
        if (long == ChadInstance.cli.ourUser.longID)
            return true
        return threadHash[long] == null || threadHash[long]!!.size < 3
    }
}