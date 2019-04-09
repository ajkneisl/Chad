package org.woahoverflow.chad.framework.handle

import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.core.ChadVar
import sx.blah.discord.handle.obj.ActivityType
import sx.blah.discord.handle.obj.StatusType
import sx.blah.discord.util.RequestBuffer

/**
 * Manages the presences obtained from the woahoverflow API
 */
object PresenceHandler {
    data class RPSet(val activityType: ActivityType, val statusType: StatusType, val status: String)

    /**
     * The list of presences
     */
    @JvmStatic
    var presences = ArrayList<RPSet>()

    /**
     * Gets the presences from the API
     */
    @JvmStatic
    fun refreshPresences() {
        val obj = JsonHandler.read("https://api.woahoverflow.org/app/presences") ?: return run {
            ChadInstance.getLogger().error("There was an issue getting presences from the API!")
        }

        val data = obj.getJSONObject("message").getJSONArray("contents")

        if (data.length() == 0) {
            ChadInstance.getLogger().error("There's no presences on API!")
            return
        }

        for (i in 0 until data.length()) {
            val ob = data.getJSONObject(i)

            if (ob.has("activityType") && ob.has("status") && ob.has("statusType")) {
                val activityType = when (ob.getInt("activityType")) {
                    1 -> ActivityType.PLAYING
                    2 -> ActivityType.LISTENING
                    3 -> ActivityType.WATCHING
                    4 -> ActivityType.STREAMING

                    else -> return
                }

                val statusType = when (ob.getInt("statusType")) {
                    1 -> StatusType.ONLINE
                    2 -> StatusType.INVISIBLE
                    3 -> StatusType.DND
                    4 -> StatusType.IDLE

                    else -> return
                }

                presences.add(RPSet(activityType, statusType, ob.getString("status")))
            } else ChadInstance.getLogger().error("There was an issue with one of the presence entries!")
        }
    }

    /**
     * Updates the presence to the inputted sett`
     */
    @JvmStatic
    fun updatePresence(presence: RPSet) {
        ChadVar.statusType = presence.statusType
        ChadVar.activityType = presence.activityType
        ChadVar.currentStatus = presence.status

        RequestBuffer.request {
            ChadInstance.getLogger().debug("Changing presence to {}, {}, {}", presence.statusType, presence.activityType, presence.status)
            ChadInstance.cli.changePresence(presence.statusType, presence.activityType, presence.status)
        }
    }

    /**
     * Updates the current presence to a random one
     */
    @JvmStatic
    fun randomPresence() {
        try {
            updatePresence(presences.random())
        } catch (e: Exception) {
            ChadInstance.getLogger().error("There's no presences in list, setting to default!")
            updatePresence(RPSet(ActivityType.PLAYING, StatusType.ONLINE, "Minecraft"))
        }
    }
}