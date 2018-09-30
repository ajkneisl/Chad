package com.jhobot;

import com.jhobot.handle.DB;
import com.jhobot.handle.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JhoBot {
    public static void main(String[] args)
    {
        if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("updatever"))
            {
                if (args[1].equalsIgnoreCase("current"))
                {
                    try {
                        JSON.set("version", "unstable-0.1.05");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        JSON.set("version", args[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
        }
        /*
        File Checking
         */
        try
        {
            File dir = new File(System.getenv("appdata") + "\\jho");
            if (!dir.exists())
                dir.mkdirs();
            File bot = new File(dir + "\\bot.json");
            if (!new File(dir + "\\bot.json").exists())
            {
                bot.createNewFile();
                JSONObject obj = new JSONObject();
                obj.put("token", "");
                obj.put("playing", "");
                obj.put("default_prefix", "");
                obj.put("steam_api_token", "");
                obj.put("version", "unstable-0.1.04");
                obj.put("uri_link", "");
                try (FileWriter filew = new FileWriter(bot)) {
                    filew.write(obj.toJSONString());
                    filew.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            File dir2 = new File(System.getenv("appdata") + "\\jho\\catpictures");
            if (!dir2.exists())
                dir2.mkdirs();
            File catpic = new File(System.getenv("appdata") + "\\jho\\catpictures.json");
            if (!catpic.exists())
            {
                catpic.createNewFile();
                JSONObject obj = new JSONObject();
                obj.put("amount", "0");
                obj.put("catgallery", new JSONArray());
                try (FileWriter filew = new FileWriter(catpic))
                {
                    filew.write(obj.toJSONString());
                    filew.flush();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        } catch (IOException e)
        {
            System.out.println("There was an error creating files during startup!");
            e.printStackTrace();
        }

        /*
        Creates bot
         */
        if (JSON.get("token").equals(""))
        {
            System.err.println("No Token!");
            System.exit(1);
        }
        IDiscordClient cli = new ClientBuilder().withToken(JSON.get("token")).withRecommendedShardCount().build();
        cli.login();
        cli.getDispatcher().registerListener(new Listener());


        /*
        Stats Handler
         */
        DB db = new DB(JSON.get("uri_link")).getSeperateCollection("stats");
    }

    public static List<Long> allowedUsers()
    {
        // bot staff ? whatever you wanna call it
        List<Long> l = new ArrayList<>();
        l.add(Long.parseLong("416399667094618124"));
        l.add(Long.parseLong("274712215024697345"));
        return l;
    }
}
