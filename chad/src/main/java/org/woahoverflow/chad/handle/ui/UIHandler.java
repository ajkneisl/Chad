package org.woahoverflow.chad.handle.ui;

import org.woahoverflow.chad.handle.ui.panels.GuildPanel;
import org.woahoverflow.chad.core.ChadBot;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.Util;
import org.woahoverflow.chad.handle.logging.LogLevel;
import org.woahoverflow.chad.handle.ui.panels.MainPanel;
import org.woahoverflow.chad.handle.ui.panels.PopUpPanel;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

@SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
public class UIHandler
{
    // panels
    private int i = 0;
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
        mainFrame.setResizable(false);
        beginMainFrame();

        // UI Updater (updates stats)
        ChadVar.EXECUTOR_POOL.submit(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 60000*5,0));
    }

    public void addLog(String log, LogLevel level)
    {
        mainpanel.logs.append("\n" +"["+ level +"] "+ log);
    }
    void newError(String error)
    {
        PopUpPanel panel = new PopUpPanel();
        JFrame frame = new JFrame("Error : Chad");
        panel.errorContent.setEditable(false);
        panel.errorContent.setText(error);
        frame.getContentPane().add(panel);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(488, 239);
        panel.guildButton.setVisible(false);
        panel.exitButton.addActionListener((ActionEvent) -> frame.dispose());

    }

    void newError(String error, IGuild guild)
    {
        PopUpPanel panel = new PopUpPanel();
        JFrame frame = new JFrame("Error : " + guild.getStringID());
        frame.getContentPane().add(panel);
        frame.setVisible(true);
        panel.errorContent.setEditable(false);
        panel.errorContent.setText(error);
        frame.setSize(488, 239);
        panel.guildButton.addActionListener((ActionEvent) -> loadGuild(guild));
        panel.exitButton.addActionListener((ActionEvent) -> frame.dispose());
    }

    public void loadGuild(IGuild guild)
    {
        JFrame frame = new JFrame("Guild : " + guild.getStringID());
        GuildPanel panel = new GuildPanel();
        frame.setVisible(true);
        frame.getContentPane().add(panel);
        frame.pack();
        panel.exitButton.addActionListener((ActionEvent) -> frame.dispose());
        panel.guildNameVal.setText(guild.getName());
        panel.leaveButton.addActionListener((ActionEvent) -> guild.leave());
        if (!guild.getClient().getOurUser().getPermissionsForGuild(guild).contains(Permissions.CREATE_INVITE))
        {
            panel.inviteLinkVal.setText("Bot doesn't have permission");
        }
        else {
            panel.inviteLinkVal.setText("Invite URL"); // TODO make invite url
        }
        panel.reCacheButton.addActionListener((ActionEvent) -> ChadVar.CACHE_DEVICE.cacheGuild(guild));
    }

    private void beginMainFrame()
    {
        mainpanel.getAllGuilds.addActionListener((ActionEvent) -> {
          i = 0;
          ChadBot.cli.getGuilds().forEach((g) -> {
                add();
                addLog("<" + i + "> "+g.getName()+" ["+g.getStringID()+"]", LogLevel.INFO);
          });
        });
        mainpanel.exitButton.addActionListener((ActionEvent) -> System.exit(0));
        mainpanel.RefreshButton.addActionListener((ActionEvent) -> update());
        mainpanel.RefreshButton2.addActionListener((ActionEvent) -> ChadVar.CACHE_DEVICE.reCacheAll());
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
                java.lang.management.ManagementFactory.getOperatingSystemMXBean();

        String memory = Util.humanReadableByteCount(os.getTotalPhysicalMemorySize(), true);

        int available_processors = os.getAvailableProcessors();
        IShard shard = ChadBot.cli.getShards().get(0);
        long ping = shard.getResponseTime();

        mainpanel.guildGo.addActionListener((ActionEvent) ->
        {
            try{
                loadGuild(ChadBot.cli.getGuildByID(Long.parseLong(mainpanel.guildList.getText().trim())));
            } catch (java.lang.NullPointerException | NumberFormatException e)
            {
                newError("Invalid Guild");
            }
        });
        mainpanel.coresVal.setText(Integer.toString(available_processors));
        mainpanel.memoryVal.setText(memory);
        mainpanel.shardRespTimeVal.setText(ping + "ms");
        mainpanel.lastReCacheAllValue.setText(ChadVar.LAST_CACHE_ALL);
        mainpanel.presenceVal.setText("Loading");
        mainpanel.logs.setEditable(false);
        mainpanel.logs.setText("UI has started.");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(1157, 842);
    }

    public void update()
    {
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
                java.lang.management.ManagementFactory.getOperatingSystemMXBean();

        String memory = Util.humanReadableByteCount(os.getTotalPhysicalMemorySize(), true);

        int available_processors = os.getAvailableProcessors();
        IShard shard = ChadBot.cli.getShards().get(0);
        long ping = shard.getResponseTime();
        mainpanel.allGuildsValue.setText(StatsHandler.getStats(cli).get("guildAmount"));
        mainpanel.biggestGuildValue.setText(StatsHandler.getStats(cli).get("biggestGuild"));
        mainpanel.botToUserVal.setText(StatsHandler.getStats(cli).get("botToPlayer"));
        mainpanel.lastReCacheAllValue.setText(ChadVar.LAST_CACHE_ALL);
        ChadVar.THREAD_HANDLER.getMap().forEach((k, v) -> v.forEach((val) -> add()));
        mainpanel.threadVal.setText(Integer.toString(ChadVar.THREAD_HANDLER.getMap().size()));
        if (ChadBot.cli.isReady())
            mainpanel.presenceVal.setText(ChadBot.cli.getOurUser().getPresence().getText().get());
        mainpanel.shardRespTimeVal.setText(Long.toString(ping) + "ms");
        mainpanel.coresVal.setText(Integer.toString(available_processors));
        mainpanel.memoryVal.setText(memory);
    }

    public void add()
    {
        this.i++;
    }
}
