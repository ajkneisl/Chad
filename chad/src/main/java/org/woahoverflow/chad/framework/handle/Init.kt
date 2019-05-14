package org.woahoverflow.chad.framework.handle

import com.google.common.net.HttpHeaders
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.sync.sync
import org.woahoverflow.chad.framework.util.Util
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

    val initInstance = Init()
    initInstance.timers()
    initInstance.data()

    if (initInstance.start()) ChadInstance.getLogger().info("Completed initialization! Took {}ms", System.currentTimeMillis()-start)
}

class Init {
    private val threads = ArrayList<Job>()

    fun start(): Boolean {
        var active = true

        while (active) {
            var threadAlive = false
            for (thread in threads) if (thread.isActive) threadAlive = true

            active = threadAlive
        }


        return true
    }

    /**
     * Initializes Timers
     */
    fun timers() {
        val job = GlobalScope.launch(CoroutineName("Initialization Handler")) {
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

                    ChadInstance.getLogger().debug("Reset all Reddit data!")
                }
            }, 0, 86400 * 1000)
        }

        threads.add(job)
    }

    /**
     * Initializes stored data
     */
    fun data() {
        val job = GlobalScope.launch(CoroutineName("Initialization Handler")) { dataRequest() }

        threads.add(job)
    }

    private fun dataRequest() {
        Objects.requireNonNull<JSONArray>(JsonHandler.readArray("https://cdn.woahoverflow.org/data/chad/swears.json")).forEach { word -> ChadVar.swearWords.add(word as String) }
        Objects.requireNonNull<JSONArray>(JsonHandler.readArray("https://cdn.woahoverflow.org/data/chad/8ball.json")).forEach { word -> ChadVar.eightBallResults.add(word as String) }
        Util.refreshDevelopers()

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
    }
}