package org.woahoverflow.chad.framework.util

import org.json.JSONArray
import org.json.JSONObject
import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.JsonHandler
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.util.RequestBuffer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection

/**
 * The Utility class for Chad
 *
 * @author sho, codebasepw
 */
object Util {

    /**
     * The user agent
     */
    private val USER_AGENT = "Mozilla/5.0"

    /**
     * Gets the current timestamp
     *
     * @return The current timestamp
     */
    val timeStamp: String = SimpleDateFormat("MM/dd/yyyy hh:mm").format(Calendar.getInstance().time)

    /**
     * Gets a String from an http
     *
     * @param url The URL to request
     * @return The gotten String
     */
    @Synchronized
    fun httpGet(url: String): String {
        try {
            val obj = URL(url)
            val con = obj.openConnection() as HttpsURLConnection
            // optional default is GET
            con.requestMethod = "GET"
            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT)
            val responseCode = con.responseCode
            if (responseCode != 200) {
                ChadInstance.getLogger().error("Failed to send a request to url {}\nResponse Code : {}", url, responseCode)
                return ""
            }
            ChadInstance.getLogger().debug("Fulfilled a request at URL : {}", url)
            val `in` = BufferedReader(
                    InputStreamReader(con.inputStream, StandardCharsets.UTF_8))

            val resp = StringBuilder()
            for (l in `in`.lines()) resp.append("$l ")
            `in`.close()
            return resp.toString().removeSuffix(" ")
        } catch (e: Exception) {
            return ""
        }
    }

    fun fixEnumString(input: String): String {
        return input.substring(0, 1).toUpperCase() + input.substring(1)
    }


    /**
     * Checks if a guild exists within the Client
     *
     * @param cli The client
     * @param guild The guild's ID
     * @return If it exists/still exists
     */
    fun guildExists(cli: IDiscordClient, guild: Long?): Boolean =  RequestBuffer.request<List<IGuild>> { cli.guilds }.get().stream().anyMatch { g -> g.longID == guild }

    fun howOld(searchTimestamp: Long): Long = Math.abs(System.currentTimeMillis() - searchTimestamp)

    /**
     * Turns ms into a seconds, day and hours format
     *
     * @param ms The time
     * @return The formatted string`
     */
    fun fancyDate(ms: Long): String {
        var response = ""

        val seconds = ms / 1000

        if (seconds <= 60) {
            // Assuming there's multiple seconds
            return "$seconds seconds"
        }

        val minutes = seconds / 60

        if (minutes < 60) {
            response = if (minutes > 1) String.format("%s minutes %s seconds", minutes, seconds - minutes * 60) else String.format("%s minute %s seconds", minutes, seconds - minutes * 60)
            return response
        }

        val hours = minutes / 60
        val hoursMinutes = minutes - hours * 60

        if (hours < 24) {
            response += if (hours > 1) String.format("%s hours ", hours) else String.format("%s hour ", hours)
            response += if (hoursMinutes > 1) String.format("%s minutes", hoursMinutes) else String.format("%s minute", hoursMinutes)
            return response
        }

        val days = hours / 24
        val hoursDays = hours - days * 24

        if (days < 7) {
            response += if (days > 1) String.format("%s days ", days) else String.format("%s day ", days)
            response += if (hoursDays > 1) String.format("%s hours", hoursDays) else String.format("%s hour", hoursDays)
            return response
        }

        val weeks = days / 7
        val weekDays = days - weeks * 7

        response += if (weeks > 1) String.format("%s weeks ", weeks) else String.format("%s week ", weeks)
        response += if (weekDays > 1) String.format("%s days", weekDays) else String.format("%s day", weekDays)
        return response
    }

    fun buildString(string: String, vararg strings: String): String {
        var str = string
        var i = 0
        val obj = Arrays.asList(*strings)

        while (str.contains("<>")) {
            str = str.replaceFirst("<>".toRegex(), obj[i])
            i++
        }

        return str
    }

    fun formatNumber(i: Int): String {
        val format = DecimalFormat("#, ###")
        val formattedString = format.format(i.toLong()).trim { it <= ' ' }

        val suffixes = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
        return when (i % 100) {
            11, 12, 13 -> formattedString + "th"
            else -> formattedString + suffixes[i % 10]
        }
    }

    /**
     * If any = true, then if any of the string start with the specified string it'll return true. If false, all strings must start with the specified string.
     */
    fun startsWith(str: String, vararg strings: String, any: Boolean = true): Boolean {
        for (string in strings) {
            if (any && str.startsWith(string)) return true

            if (!any && !str.startsWith(string)) return false
        }

        return !any
    }

    /**
     * Refreshes the original developer list using the woahoverflow cdn.
     *
     * These users are marked as trusted, and cannot be removed from the developer list.
     */
    fun refreshDevelopers() {
        ChadVar.ORIGINAL_DEVELOPERS.clear()
        Objects.requireNonNull<JSONArray>(JsonHandler.readArray("https://cdn.woahoverflow.org/data/contributors.json")).forEach { v ->

            if ((v as JSONObject).getBoolean("developer")) {
                ChadInstance.getLogger().debug("User ${v.getString("display_name")} has been given the role of Developer.")
                ChadVar.ORIGINAL_DEVELOPERS.add(v.getLong("id"))
            }
        }
    }
}
