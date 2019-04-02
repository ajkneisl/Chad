package org.woahoverflow.chad.framework.handle

import com.google.common.net.HttpHeaders
import org.json.JSONArray
import org.json.JSONObject
import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.sync.sync
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

/** This class sorts all initialization procedures @author sho */

/**
 * Main initialization, does everything
 */
fun init() {
    if (!ChadInstance.cli.isReady) {
        ChadInstance.getLogger().warn("Discord Client is not ready, waiting")
        while (!ChadInstance.cli.isReady) {
            TimeUnit.MILLISECONDS.sleep(500)
            ChadInstance.getLogger().warn("Discord Client is still not ready, waiting")
        }
    }

    val start = System.currentTimeMillis()
    ChadInstance.getLogger().info("Starting initialization...")

    Init.timers()
    Init.data()

    if (Init.start()) ChadInstance.getLogger().info("Completed initialization! Took {}ms", System.currentTimeMillis()-start)
}

object Init {
    private val threads = ArrayList<Thread>()

    @JvmStatic
    fun start(): Boolean {
        var active = true

        for (thread in threads) thread.start()

        while (active) {
            var threadAlive = false
            for (thread in threads) if (thread.isAlive) threadAlive = true

            active = threadAlive
        }


        return true
    }

    /**
     * Initializes Timers
     */
    @JvmStatic
    fun timers() {
        threads.add(Thread {
            val timer = ChadInstance.getTimer()

            /**
             * Syncs with the website
             * Updates money leaderboard
             *
             * TODO add other leaderboard types
             *
             * # 5 minutes
             */
            timer.schedule(object : TimerTask(){
                override fun run() {
                    sync(ChadInstance.cli)

                    PresenceHandler.refreshPresences()

                    LeaderboardHandler.refreshLeaderboard(LeaderboardHandler.LeaderboardType.MONEY)
                    LeaderboardHandler.refreshLeaderboard(LeaderboardHandler.LeaderboardType.XP)
                }
            }, 0, 1000*60*5)

            /**
             * Rotates presence
             *
             * Separated due to required variable
             *
             * # 5 minutes
             */
            if (ChadVar.rotatePresence) {
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        if (!ChadVar.rotatePresence) return

                        PresenceHandler.randomPresence()
                    }
                }, 0, 1000 * 60 * 5)
            }

            /**
             * Clears the Reddit cache
             *
             * # 1 day
             */
            timer.schedule(object : TimerTask() {
                override fun run() {
                    Reddit.subreddits.clear()

                    ChadInstance.getLogger().debug("Reset all cached subreddits!")
                }
            }, 0, 86400 * 1000)
            /**
             * Users can only have 3 running threads, and this makes sure of it
             *
             * # 1 second
             */
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (!ThreadHandler.threadHash.isEmpty()) {
                        ThreadHandler.threadHash.forEach {
                            user, array ->
                            run {
                                if (!array.isEmpty()) {
                                    var i = 0

                                    while (array.size > i) {
                                        if (array[i].isDone) {
                                            array.removeAt(i)
                                            ThreadHandler.runningThreads--
                                        }
                                        i++
                                    }
                                } else {
                                    ThreadHandler.threadHash.remove(user)
                                }
                            }
                        }
                    }
                }
            }, 0, 1000)
        })
    }

    /**
     * Initializes stored data
     */
    @JvmStatic
    fun data() {
        threads.add(Thread {
            Objects.requireNonNull<JSONArray>(JsonHandler.readArray("https://cdn.woahoverflow.org/data/chad/swears.json")).forEach { word -> ChadVar.swearWords.add(word as String) }
            Objects.requireNonNull<JSONArray>(JsonHandler.readArray("https://cdn.woahoverflow.org/data/chad/8ball.json")).forEach { word -> ChadVar.eightBallResults.add(word as String) }
            Objects.requireNonNull<JSONArray>(JsonHandler.readArray("https://cdn.woahoverflow.org/data/contributors.json")).forEach { v ->
                if ((v as JSONObject).getBoolean("allow")) {
                    ChadInstance.getLogger().debug("Added user " + v.getString("display_name") + " to group System Administrator")
                    ChadVar.DEVELOPERS.add(v.getLong("id"))
                } else {
                    ChadInstance.getLogger().debug("Avoided adding user " + v.getString("display_name"))
                }
            }

            try {
                val url = URL("https://cdn.woahoverflow.org/data/chad/words.txt")
                val con = url.openConnection() as HttpsURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("User-Agent", HttpHeaders.USER_AGENT)
                val `in` = BufferedReader(InputStreamReader(con.inputStream, StandardCharsets.UTF_8))
                val ar = ArrayList<String>()
                `in`.lines().forEach { ar.add(it) }
                ChadVar.wordsList = ar
                `in`.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
        })
    }
}