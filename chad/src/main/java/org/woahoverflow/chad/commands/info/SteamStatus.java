package org.woahoverflow.chad.commands.info;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.handle.JsonHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * This class is very inefficient due to it making 4 requests
 *
 * TODO Make this more efficient with caching
 *
 * @author sho
 */
public class SteamStatus implements Command.Class {
    @NotNull
    @Override
    public Runnable run(@NotNull MessageEvent e, @NotNull List<String> args) {
        return () -> {
            JSONObject status = JsonHandler.INSTANCE.read("https://steamgaug.es/api/v2");
            EmbedBuilder embedBuilder = new EmbedBuilder();

            // All of the statistics from SteamGauge
            String steamCommunity = Objects.requireNonNull(status).getJSONObject("SteamCommunity").getInt("online") == 1 ? "online" : "offline";
            String steamCommunityLatency = status.getJSONObject("SteamCommunity").getInt("time") + "ms";

            String steamStore = status.getJSONObject("SteamStore").getInt("online") == 1 ? "online" : "offline";
            String steamStoreLatency = status.getJSONObject("SteamStore").getInt("time") + "ms";

            String steamApi = status.getJSONObject("ISteamUser").getInt("online") == 1 ? "online" : "offline";
            String steamApiLatency = status.getJSONObject("ISteamUser").getInt("time") + "ms";

            // Adds commas into a number
            DecimalFormat formatter = new DecimalFormat("#,###");

            embedBuilder.withDesc(
                "Steam Community: `"+steamCommunity+"` ("+steamCommunityLatency+ ')' +
                    "\nSteam Store: `"+steamStore+"` ("+steamStoreLatency+ ')' +
                    "\nSteam API: `"+steamApi+"` ("+steamApiLatency+ ')' +
                    "\n\nCS-GO: `"+formatter.format(Objects.requireNonNull(JsonHandler.INSTANCE.read("https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=730&key=" + ChadVar.STEAM_API_KEY)).getJSONObject("response").getInt("player_count"))+"` players" +
                    "\nPUBG: `"+formatter.format(Objects.requireNonNull(JsonHandler.INSTANCE.read("https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=578080&key=" + ChadVar.STEAM_API_KEY)).getJSONObject("response").getInt("player_count"))+"` players" +
                    "\nDota 2: `"+formatter.format(Objects.requireNonNull(JsonHandler.INSTANCE.read("https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=570&key=" + ChadVar.STEAM_API_KEY)).getJSONObject("response").getInt("player_count"))+"` players" +
                    "\nTF 2: `"+formatter.format(Objects.requireNonNull(JsonHandler.INSTANCE.read("https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=440&key=" + ChadVar.STEAM_API_KEY)).getJSONObject("response").getInt("player_count"))+"` players"
            );

            new MessageHandler(e.getChannel(), e.getAuthor()).credit("steamguag.es").sendEmbed(embedBuilder);
        };
    }

    @NotNull
    @Override
    public Runnable help(@NotNull MessageEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("steamstatus", "Check if steam is online, and how many players there are in popular games.");
        return Command.helpCommand(st, "Steam Status", e);
    }
}
