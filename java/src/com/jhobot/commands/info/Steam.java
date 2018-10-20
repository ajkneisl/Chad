package com.jhobot.commands.info;

import com.jhobot.core.JhoBot;
import com.jhobot.handle.JSONHandler;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Steam implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            String key = JhoBot.JSON_HANDLER.get("steam_api_token");

            if (args.size() == 0 || args.size() == 1)
            {
                m.sendError("Invalid Arguments");
                return;
            }

            try {
                // builds steam profile
                String arg = args.get(0);
                int success = JhoBot.JSON_HANDLER.read("https://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=" + key + "&vanityurl=" + args.get(1)).getJSONObject("response").getInt("success");

                if (success != 1)
                {
                    m.sendError("Invalid Steam Profile!");
                    return;
                }

                String steamid = JhoBot.JSON_HANDLER.read("https://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=" + key + "&vanityurl=" + args.get(1)).getJSONObject("response").getString("steamid");
                JSONObject obj = JhoBot.JSON_HANDLER.read("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + key + "&steamids=" + steamid).getJSONObject("response").getJSONArray("players").getJSONObject(0);
                SteamProfile profile = new SteamProfile() {
                    @Override
                    public String getName() {
                        return obj.getString("personaname");
                    }

                    @Override
                    public String getID() {
                        return steamid;
                    }

                    @Override
                    public JSONArray getCSGOStats() {
                        try {
                            return JhoBot.JSON_HANDLER.read("https://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=730&key=" + key + "&steamid=" + steamid).getJSONObject("playerstats").getJSONArray("stats");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    public JSONObject getProfileObj() {
                        try {
                            return JhoBot.JSON_HANDLER.read("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + key + "&steamids=" + steamid).getJSONObject("response").getJSONArray("players").getJSONObject(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                };

                if (arg.equalsIgnoreCase("profile"))
                {
                    EmbedBuilder b2 = new EmbedBuilder();
                    b2.withTitle("Steam Profile : " + profile.getName());
                    b2.withImage(obj.getString("avatarfull"));
                    b2.appendField("SteamID", profile.getID(), true);
                    b2.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
                    b2.withFooterText(Util.getTimeStamp());

                    m.sendEmbed(b2.build());
                } else if (arg.equalsIgnoreCase("csgo"))
                {
                    EmbedBuilder b2 = new EmbedBuilder();
                    b2.withFooterIcon(obj.getString("avatar"));
                    b2.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));

                    try {
                        profile.getCSGOStats().getJSONObject(0);
                    } catch (NullPointerException ee)
                    {
                        m.sendError("Private Steam Profile!");
                        return;
                    }
                    if (args.size() == 2)
                    {
                        b2.withTitle("Map Stats for " + profile.getName());
                        b2.appendField("Total Kills", Integer.toString(profile.getCSGOStats().getJSONObject(0).getInt("value")), true);
                        b2.appendField("Total Deaths", Integer.toString(profile.getCSGOStats().getJSONObject(1).getInt("value")), true);
                        b2.appendField("Total Time Played", Integer.toString(profile.getCSGOStats().getJSONObject(2).getInt("value")/60/60) + "hrs", true);
                        b2.appendField("Total Bombs Planted", Integer.toString(profile.getCSGOStats().getJSONObject(3).getInt("value")), true);
                        b2.appendField("Total Bombs Defused", Integer.toString(profile.getCSGOStats().getJSONObject(4).getInt("value")), true);
                        b2.appendField("Total Wins", Integer.toString(profile.getCSGOStats().getJSONObject(5).getInt("value")), true);
                        b2.appendField("Total Damage Done", Integer.toString(profile.getCSGOStats().getJSONObject(6).getInt("value")), true);
                        b2.appendField("Total Matches Won", Integer.toString(profile.getCSGOStats().getJSONObject(112).getInt("value")), true);
                        b2.appendField("Total Matches Played", Integer.toString(profile.getCSGOStats().getJSONObject(113).getInt("value")), true);
                    }
                    else if (args.size() == 3 && args.get(2).equalsIgnoreCase("kills"))
                    {
                        b2.withTitle("Map Stats for " + profile.getName());
                        b2.appendField("Knife", Integer.toString(profile.getCSGOStats().getJSONObject(9).getInt("value")), true);
                        b2.appendField("Taser", Integer.toString(profile.getCSGOStats().getJSONObject(166).getInt("value")), true);
                        b2.appendField("HE Grenade", Integer.toString(profile.getCSGOStats().getJSONObject(10).getInt("value")), true);
                        b2.appendField("Glock", Integer.toString(profile.getCSGOStats().getJSONObject(11).getInt("value")), true);
                        b2.appendField("Deagle", Integer.toString(profile.getCSGOStats().getJSONObject(12).getInt("value")), true);
                        b2.appendField("USP-S", Integer.toString(profile.getCSGOStats().getJSONObject(12).getInt("value")), true);
                        b2.appendField("Mac-10", Integer.toString(profile.getCSGOStats().getJSONObject(16).getInt("value")), true);
                        b2.appendField("UMP-45", Integer.toString(profile.getCSGOStats().getJSONObject(17).getInt("value")), true);
                        b2.appendField("AWP", Integer.toString(profile.getCSGOStats().getJSONObject(19).getInt("value")), true);
                        b2.appendField("AK-47", Integer.toString(profile.getCSGOStats().getJSONObject(20).getInt("value")), true);
                        b2.appendField("M4", Integer.toString(profile.getCSGOStats().getJSONObject(162).getInt("value")), true);
                    } else if (args.size() == 3 && args.get(2).equalsIgnoreCase("maps"))
                    {
                        b2.withTitle("Map Stats for " + profile.getName());
                        b2.appendField("Office", Integer.toString(profile.getCSGOStats().getJSONObject(28).getInt("value")), true);
                        b2.appendField("Cobble", Integer.toString(profile.getCSGOStats().getJSONObject(29).getInt("value")), true);
                        b2.appendField("Dust 2", Integer.toString(profile.getCSGOStats().getJSONObject(30).getInt("value")), true);
                        b2.appendField("Inferno", Integer.toString(profile.getCSGOStats().getJSONObject(31).getInt("value")), true);
                        b2.appendField("Train", Integer.toString(profile.getCSGOStats().getJSONObject(33).getInt("value")), true);
                        b2.appendField("Nuke", Integer.toString(profile.getCSGOStats().getJSONObject(32).getInt("value")), true);
                    } else if (args.size() == 3 && args.get(2).equalsIgnoreCase("lastmatch"))
                    {
                        b2.withTitle(profile.getName() + "'s last game");
                        b2.appendField("Round Wins", Integer.toString(profile.getCSGOStats().getJSONObject(83).getInt("value")), true);
                        b2.appendField("Kills", Integer.toString(profile.getCSGOStats().getJSONObject(85).getInt("value")), true);
                        b2.appendField("Deaths", Integer.toString(profile.getCSGOStats().getJSONObject(86).getInt("value")), true);
                        b2.appendField("MVPs", Integer.toString(profile.getCSGOStats().getJSONObject(87).getInt("value")), true);

                    }
                    else {
                        b2.withTitle("Invalid stat type!");
                    }
                    b2.withFooterText(Util.getTimeStamp());

                    m.sendEmbed(b2.build());
                } else {
                    m.sendError("Invalid Arguments!");
                }
            } catch (IOException ee)
            {
                if (ee.getMessage().contains("429"))
                {
                    m.sendError("Too Many Requests!");
                    return;
                }
                m.sendError("There was an error with that request!");
                ee.printStackTrace();
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("steam profile <steam name>", "Gets a steam user's profile.");
        st.put("steam csgo <steam name> [kills/maps/lastmatch]", "Gets a steam user's CS:GO stats.");
        return HelpHandler.helpCommand(st, "Steam", e);
    }
}

interface SteamProfile
{
    String getName();
    String getID();
    JSONArray getCSGOStats();
    JSONObject getProfileObj();
}
