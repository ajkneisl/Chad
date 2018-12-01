package org.woahoverflow.chad.framework.ui;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import org.woahoverflow.chad.core.ChadBot;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Util;
import org.woahoverflow.chad.framework.ui.panels.GuildPanel;
import org.woahoverflow.chad.framework.ui.panels.MainPanel;
import org.woahoverflow.chad.framework.ui.panels.PopUpPanel;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UIHandler
{
    public static UIHandler handle;
    // panels
    private int i;
    private final JFrame mainFrame = new JFrame("Chad");
    private final MainPanel mainpanel = new MainPanel();
    private final IDiscordClient cli;
    public UIHandler(IDiscordClient cli)
    {

        this.cli = cli;
        // mainpanel
        mainFrame.getContentPane().add(mainpanel);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // makes it look better :)
        } catch (@SuppressWarnings("all") Exception e) { // it needs to shut the fuck up
            e.printStackTrace();
        }
        mainFrame.setVisible(true);
        mainFrame.pack();
        mainFrame.setResizable(false);
        beginMainFrame();

        // UI Updater (updates stats)
        Chad.runThread(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 60000*5,0), Chad.getInternalConsumer());
    }

    public final void addLog(String log, LogLevel level)
    {
        mainpanel.logs.append('\n' +"["+ level +"] "+ log);
    }
    static void newError(String error)
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
        panel.exitButton.addActionListener((ev) -> frame.dispose());

    }

    @SuppressWarnings("unused")
    static void newError(String error, IGuild guild)
    {
        PopUpPanel panel = new PopUpPanel();
        JFrame frame = new JFrame("Error : " + guild.getStringID());
        frame.getContentPane().add(panel);
        frame.setVisible(true);
        panel.errorContent.setEditable(false);
        panel.errorContent.setText(error);
        frame.setSize(488, 239);
        panel.guildButton.addActionListener((ev) -> loadGuild(guild));
        panel.exitButton.addActionListener((ev) -> frame.dispose());
    }

    public static void loadGuild(IGuild guild)
    {
        JFrame frame = new JFrame("Guild : " + guild.getStringID());
        GuildPanel panel = new GuildPanel();
        frame.setVisible(true);
        frame.getContentPane().add(panel);
        frame.pack();
        panel.exitButton.addActionListener((ev) -> frame.dispose());
        panel.guildNameVal.setText(guild.getName());
        panel.leaveButton.addActionListener((ev) -> {
            guild.leave();
            frame.dispose();
        });
        if (!guild.getClient().getOurUser().getPermissionsForGuild(guild).contains(Permissions.CREATE_INVITE))
        {
            panel.inviteLinkVal.setText("Bot doesn't have permission");
        }
        else {
            // creates an invite (watch out,it can spam it)
            String invite = guild.getClient().getOurUser().getPermissionsForGuild(guild)
                .contains(Permissions.CREATE_INVITE) ? "https://discord.gg/" + guild
                .getDefaultChannel().createInvite(60, 100, false, true).getCode()
                : "No Permission for Invite!";
            panel.inviteLinkVal.setText(invite);
        }
        panel.reCacheButton.addActionListener((ev) -> Chad.getGuild(guild).cache());
    }

    @SuppressWarnings("all")
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
        mainpanel.refreshButton.addActionListener((ActionEvent) -> update());
        // three lambdas in one :)
        mainpanel.refreshButton2.addActionListener((ActionEvent) -> RequestBuffer.request(() -> ChadBot.cli.getGuilds().forEach((guild) -> Chad.getGuild(guild).cache())));

        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
                java.lang.management.ManagementFactory.getOperatingSystemMXBean();

        String memory = Util.humanReadableByteCount(os.getTotalPhysicalMemorySize(), true);

        int available_processors = os.getAvailableProcessors();
        long ping = 69;
        try {
            ping = ChadBot.cli.getShards().get(0).getResponseTime();
        } catch (IndexOutOfBoundsException e)
        {
            // throwaway :(
        }

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
        mainpanel.presenceVal.setText("Loading");
        mainpanel.logs.setEditable(false);
        mainpanel.logs.setText("UI has started.");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(1157, 842);
    }

    private static HashMap<String, String> getStats(IDiscordClient cli)
    {

        List<IGuild> guilds = RequestBuffer.request(cli::getGuilds).get();

        int bots = 0;
        int users = 0;
        int biggestGuildAmount = 0;
        String biggestGuildName = "";
        for (IGuild g : guilds)
        {
            if (g.getUsers().size() > biggestGuildAmount)
            {
                biggestGuildName = g.getName();
                biggestGuildAmount = g.getUsers().size();
            }
            for (IUser u : g.getUsers())
            {
                if (u.isBot()) {
                    bots++;
                } else {
                    users++;
                }
            }
        }
        HashMap<String, String> hashmap = new HashMap<>();
        hashmap.put("biggestGuild", biggestGuildName + '(' +biggestGuildAmount+ ')');
        hashmap.put("botToPlayer", bots +"/"+ users);
        hashmap.put("guildAmount", Integer.toString(guilds.size()));
        return hashmap;
    }

    public final void update()
    {
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
                java.lang.management.ManagementFactory.getOperatingSystemMXBean();

        String memory = Util.humanReadableByteCount(os.getTotalPhysicalMemorySize(), true);

        int availableProcessors = os.getAvailableProcessors();
        IShard shard = ChadBot.cli.getShards().get(0);
        long ping = shard.getResponseTime();
        mainpanel.allGuildsValue.setText(getStats(cli).get("guildAmount"));
        mainpanel.biggestGuildValue.setText(getStats(cli).get("biggestGuild"));
        mainpanel.botToUserVal.setText(getStats(cli).get("botToPlayer"));/*
        ChadVar.threadDevice.getMap().forEach((key, val) -> val.forEach((value) -> add()));
        mainpanel.threadVal.setText(Integer.toString(ChadVar.threadDevice.getMap().size()));*/
        if (ChadBot.cli.isReady() && ChadBot.cli.getOurUser().getPresence().getText().isPresent()) {
            mainpanel.presenceVal.setText(ChadBot.cli.getOurUser().getPresence().getText().get());
        }
        mainpanel.shardRespTimeVal.setText(ping + "ms");
        mainpanel.coresVal.setText(Integer.toString(availableProcessors));
        mainpanel.memoryVal.setText(memory);
    }

    @SuppressWarnings("unused")
    public enum LogLevel
    {
        INFO, WARNING, SEVERE, EXCEPTION, CACHING
    }
    private void add()
    {
        i++;
    }
}
