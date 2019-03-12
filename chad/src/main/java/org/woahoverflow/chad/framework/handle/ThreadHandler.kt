package org.woahoverflow.chad.framework.handle

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
     * Initializes the Thread Handler
     */
    @JvmStatic
    fun initThreads() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (!threadHash.isEmpty()) {
                    threadHash.forEach {
                        user, array ->
                        run {
                            if (!array.isEmpty()) {
                                var i = 0

                                while (array.size > i) {
                                    if (array[i].isDone) {
                                        array.removeAt(i)
                                        runningThreads--
                                    }
                                    i++
                                }
                            } else {
                                threadHash.remove(user)
                            }
                        }
                    }
                }
            }
        }, 0, 2)
    }

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
    fun canUserRunThread(long: Long): Boolean = threadHash[long] == null || threadHash[long]!!.size < 3
}