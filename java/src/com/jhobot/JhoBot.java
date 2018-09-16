package com.jhobot;

import com.jhobot.handle.JSON;
import org.json.simple.JSONObject;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JhoBot {
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
                obj.put("version", "0.1");
                obj.put("uri_link", "");
                try (FileWriter filew = new FileWriter(bot)) {
                    filew.write(obj.toJSONString());
                    filew.flush();
                } catch (IOException e) {
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
        IDiscordClient cli = new ClientBuilder().withToken(JSON.get("token")).build();
        cli.login();
        cli.getDispatcher().registerListener(new Listener());


    }
}
