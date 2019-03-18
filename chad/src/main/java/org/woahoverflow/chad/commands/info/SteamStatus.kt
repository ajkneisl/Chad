package org.woahoverflow.chad.commands.info

import org.json.JSONObject
import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.ArgumentHandler
import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder

import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author sho
 */
class SteamStatus : Command.Class {
    /**
     * This caches the amount of users currently on a game to result in less requests to the Steam API
     *
     * It updates every hour
     */
    private val cached = object : ConcurrentHashMap<String, String>() {
        init {
            fun update() {
                if (ArgumentHandler.isToggled("DISABLE_STEAM_CACHE")) return

                val csgo = JsonHandler.read("https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=730&key=" + ChadVar.STEAM_API_KEY)!!.getJSONObject("response").getInt("player_count").toLong()

                val pubg = JsonHandler.read("https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=578080&key=" + ChadVar.STEAM_API_KEY)!!.getJSONObject("response").getInt("player_count").toLong()

                val dota = JsonHandler.read("https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=570&key=" + ChadVar.STEAM_API_KEY)!!.getJSONObject("response").getInt("player_count").toLong()

                val tf = JsonHandler.read("https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=440&key=" + ChadVar.STEAM_API_KEY)!!.getJSONObject("response").getInt("player_count").toLong()

                val formatter = DecimalFormat("#,###")

                this["csgo"] = formatter.format(csgo)
                this["pubg"] = formatter.format(pubg)
                this["dota"] = formatter.format(dota)
                this["tf"] = formatter.format(tf)
            }

            Timer().schedule(object : TimerTask(){
                override fun run() {
                    update()
                }
            }, 0, 1000*60*60) // One hour
        }
    }
    override fun run(e: MessageEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val status = JsonHandler.read("https://steamgaug.es/api/v2")
            val embedBuilder = EmbedBuilder()

            // All of the statistics from SteamGauge
            val steamCommunity = if (Objects.requireNonNull<JSONObject>(status).getJSONObject("SteamCommunity").getInt("online") == 1) "online" else "offline"
            val steamCommunityLatency = status!!.getJSONObject("SteamCommunity").getInt("time").toString() + "ms"

            val steamStore = if (status.getJSONObject("SteamStore").getInt("online") == 1) "online" else "offline"
            val steamStoreLatency = status.getJSONObject("SteamStore").getInt("time").toString() + "ms"

            val steamApi = if (status.getJSONObject("ISteamUser").getInt("online") == 1) "online" else "offline"
            val steamApiLatency = status.getJSONObject("ISteamUser").getInt("time").toString() + "ms"

            embedBuilder.withDesc(
                    "Steam Community: `" + steamCommunity + "` (" + steamCommunityLatency + ')'.toString() +
                            "\nSteam Store: `" + steamStore + "` (" + steamStoreLatency + ')'.toString() +
                            "\nSteam API: `" + steamApi + "` (" + steamApiLatency + ')'.toString() +
                            "\n\nCSGO: `${cached["csgo"]}`" +
                            "\nPUBG: `${cached["pubg"]}`" +
                            "\nDota 2: `${cached["dota"]}`" +
                            "\nTF 2: `${cached["tf"]}`"
            )

            MessageHandler(e.channel, e.author).credit("steamguag.es").sendEmbed(embedBuilder)
        }
    }

    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["steamstatus"] = "Check if steam is online, and how many players there are in popular games."
        return Command.helpCommand(st, "Steam Status", e)
    }
}
