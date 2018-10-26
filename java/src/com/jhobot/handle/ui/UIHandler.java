package com.jhobot.handle.ui;

import sx.blah.discord.api.IDiscordClient;

import javax.swing.*;
import java.util.HashMap;

@SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
public class UIHandler
{
    private StatPanel panel = new StatPanel();
    private IDiscordClient cli;
    public UIHandler(IDiscordClient cli)
    {
        JFrame frame = new JFrame("JhoBot : Statistics");
        frame.setVisible(true);
        frame.add(this.panel);
        frame.setSize(411, 300);
        frame.setResizable(false);
        this.cli = cli;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // makes it look better :)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update()
    {
        HashMap<String, String> hash = StatsHandler.getStats(this.cli);
        this.panel.setAvgGuildSize(Integer.parseInt(hash.get("avgGuildSize")));
        this.panel.setBiggestGuild(hash.get("biggestGuild"));
        this.panel.setBotToPlayer(hash.get("botToPlayer"));
        this.panel.setGuildAmount(Integer.parseInt(hash.get("guildAmount")));
    }

    public StatPanel getPanel()
    {
        return this.panel;
    }
}
