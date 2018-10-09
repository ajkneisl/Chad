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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JhoBot {
    public static DB db = new DB(JSON.get("uri_link"));
    public static ExecutorService exec = Executors.newFixedThreadPool(15);
    public static void main(String[] args)
    {
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
                obj.put("version", "unstable-0.1.06");
                obj.put("uri_link", "");
                try (FileWriter filew = new FileWriter(bot)) {
                    filew.write(obj.toJSONString());
                    filew.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            File imgdir = new File(System.getenv("appdata") + "\\jho\\imgcache");
            if (!imgdir.exists())
                imgdir.mkdirs();
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
        cli.getDispatcher().registerListener(new com.jhobot.Listener());
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
