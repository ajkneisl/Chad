package com.jhobot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

public class JhoBot {
    public static void main(String[] args)
    {
        /*
        Creates bot
         */
        IDiscordClient cli = new ClientBuilder().withToken("NDkwNjU4MzY0ODg0NTE2ODk0.Dn8g1Q.TWi-IeS3rN8qdXGAMuAtgAz7aiw").build();
        cli.login();
        cli.getDispatcher().registerListener(new Listener());


    }
}
