package dev.shog.chad.framework.handle

import dev.shog.chad.core.ChadVar
import dev.shog.chad.core.TIMER
import dev.shog.chad.core.getClient
import dev.shog.chad.core.getLogger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

/** This class sorts all initialization procedures @author sho */

/**
 * Main initialization, does everything
 */
fun init() {
    val start = System.currentTimeMillis()
    getLogger().info("Starting initialization...")

    val initInstance = Init()
    initInstance.timers()
    initInstance.data()

    if (initInstance.start()) getLogger().info("Completed initialization! Took {}ms", System.currentTimeMillis()-start)
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
            /**
             * Syncs with the website
             * Refreshes presences
             *
             * # 5 minutes
             */
            TIMER.schedule(object : TimerTask(){
                override fun run() {
                    PresenceHandler.refreshPresences()
                }
            }, 0, 1000*60*5)

            /**
             * Updates leaderboards. (This is initially delayed to improve startup times)
             *
             * # 15 minutes
             */
            TIMER.schedule(object : TimerTask() {
                override fun run() {
                    LeaderboardHandler.refreshLeaderboard(LeaderboardHandler.LeaderboardType.MONEY)
                    LeaderboardHandler.refreshLeaderboard(LeaderboardHandler.LeaderboardType.XP)
                }
            }, 1000*60*5, 1000*60*15)

            /**
             * Rotates presence
             *
             * Separated due to required variable
             *
             * # 5 minutes
             */
            if (ChadVar.rotatePresence) {
                TIMER.schedule(object : TimerTask() {
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
            TIMER.schedule(object : TimerTask() {
                override fun run() {
                    Reddit.subreddits.clear()

                    getLogger().debug("Reset all Reddit data!")
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
        SwearWordEngine.update()
    }
}

/**
 * Manages swear words. Isn't perfect though.
 */
object SwearWordEngine {
    private val I = Pair("i", 1)
    private val A = Pair("a", 4)
    private val S = Pair("s", 5)

    /**
     * Gets words from shog.dev, and updates appropriately.
     */
    fun update() {
        JsonHandler.readArray("https://api.shog.dev/chad/swears")?.forEach { w ->
            addSwearWord(w as? String ?: "")
        } ?: getLogger().error("There was an issue getting swear words from database.") // TODO add fallback (default list maybe?)
    }

    /**
     * Adds a swear word to the main list.
     */
    private fun addSwearWord(word: String) {
        if (word.isBlank()) return

        val orientations = ArrayList<String>().apply { add(word) }

        orientations.add(change(word, I))
        orientations.add(change(word, A))
        orientations.add(change(word, S))
        orientations.add(change(word, I, A))
        orientations.add(change(word, I, S))
        orientations.add(change(word, I, S, A))
        orientations.add(change(word, S, A))

        ChadVar.swearWords.addAll(orientations)
    }

    /**
     * Replaces a word with specified letters.
     */
    private fun change(str: String, vararg sets: Pair<String, Int>): String {
        var newStr = str

        for (set in sets){
            newStr = newStr.replace(set.first, set.second.toString())
        }

        return newStr
    }
}