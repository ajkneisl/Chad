package com.jhobot.handle.ui;

import com.jhobot.core.ChadBot;
import com.jhobot.core.ChadVar;
import sx.blah.discord.api.IDiscordClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

@SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
public class UIHandler
{
    // panels
    private JFrame mainFrame = new JFrame("Chad");
    private MainPanel mainpanel = new MainPanel();
    private IDiscordClient cli;

    public UIHandler(IDiscordClient cli)
    {

        this.cli = cli;
        // mainpanel
        mainFrame.getContentPane().add(mainpanel);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // makes it look better :)
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainFrame.setVisible(true);
        mainFrame.pack();
        beginMainFrame();

        // UI Updater (updates stats)
        ChadVar.EXECUTOR_POOL.submit(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 60000*5,0));
    }

    public void addLog(String log)
    {
        mainpanel.logs.append("\n" + log);
    }
    void newError(String error)
    {
        PopUpPanel panel = new PopUpPanel();
        JFrame frame = new JFrame("Chad : Error");
        try {
            frame.setIconImage(ImageIO.read(getClass().getResource("img/ui_error.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setName("Chad : Error");
        panel.textArea1.setEditable(false);
        panel.textArea1.setText(error);
        frame.getContentPane().add(panel);
        frame.setVisible(true);
        frame.pack();
        panel.Exit.addActionListener((ActionEvent) -> frame.dispose());

    }

    private void beginMainFrame()
    {
        mainpanel.button1.addActionListener((ActionEvent) -> System.exit(0));
        mainpanel.RefreshButton.addActionListener((ActionEvent) -> update());
        mainpanel.logs.setEditable(false);
        mainpanel.logs.setText("UI has begun.");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try {
            mainFrame.setIconImage(ImageIO.read(getClass().getResource("img/ui_icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update()
    {
        mainpanel.allGuildsValue.setText(StatsHandler.getStats(cli).get("guildAmount"));
        mainpanel.biggestGuildValue.setText(StatsHandler.getStats(cli).get("biggestGuild"));
        mainpanel.botToUserVal.setText(StatsHandler.getStats(cli).get("botToPlayer"));
    }
}
