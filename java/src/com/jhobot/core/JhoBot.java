package com.jhobot.core;

import com.jhobot.handle.DB;
import com.jhobot.handle.JSON;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JhoBot {
    public String confirmJson = new JSON().check() + "";
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
                System.out.println("Created Jho Directory : " + dir.mkdirs());
            File bot = new File(dir + "\\bot.json");
            if (!new File(dir + "\\bot.json").exists())
            {
                System.out.println("Created Bot Directory : " + bot.createNewFile());
                JSONObject obj = new JSONObject();
                obj.put("token", "");
                obj.put("playing", "");
                obj.put("default_prefix", "");
                obj.put("steam_api_token", "");
                obj.put("version", "unstable-0.1.06");
                obj.put("uri_link", "");
                try (FileWriter filew = new FileWriter(bot)) {
                    filew.write(obj.toString());
                    filew.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            File imgdir = new File(System.getenv("appdata") + "\\jho\\imgcache");
            if (!imgdir.exists())
                System.out.println("Created Temp Image Directory : " + imgdir.mkdirs());
            File dir2 = new File(System.getenv("appdata") + "\\jho\\catpictures");
            if (!dir2.exists())
                System.out.println("Created Cat Pictures Directory : " + dir2.mkdirs());
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
