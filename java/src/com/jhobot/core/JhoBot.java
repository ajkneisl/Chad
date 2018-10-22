package com.jhobot.core;

import com.jhobot.handle.DatabaseHandler;
import com.jhobot.handle.JSONHandler;
import com.jhobot.handle.commands.ThreadCountHandler;
import org.json.JSONObject;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JhoBot {
    public static final JSONHandler JSON_HANDLER = new JSONHandler().forceCheck();
    public static final DatabaseHandler DATABASE_HANDLER = new DatabaseHandler(JSON_HANDLER.get("uri_link"));
    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(30);
    public static void main(String[] args)
    {
        /*
        Creates bot
         */
        if (JhoBot.JSON_HANDLER.get("token").equals(""))
        {
            System.err.println("No Token!");
            System.exit(1);
        }
        IDiscordClient cli = new ClientBuilder().withToken(JhoBot.JSON_HANDLER.get("token")).withRecommendedShardCount().build();
        cli.login();
        cli.getDispatcher().registerListener(new Listener());
    }

    public static ArrayList<Long> allowedUsers()
    {
        // bot staff ? whatever you wanna call it
        ArrayList<Long> ar = new ArrayList<>();
        ar.add(Long.parseLong("274712215024697345"));
        ar.add(Long.parseLong("274712215024697345"));
        return ar;
    }
}
