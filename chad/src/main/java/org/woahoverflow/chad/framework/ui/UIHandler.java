package org.woahoverflow.chad.framework.ui;

import java.util.concurrent.TimeUnit;
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

public class UIHandler
{
    private static boolean activeUpdate;
    public static UIHandler handle;

    // panels
    private int i;
    private final JFrame mainFrame = new JFrame("Chad");
    private final MainPanel mainpanel = new MainPanel();
    private final IDiscordClient cli;

    /**
     * Main Constructor for the UI
     * @param cli the IDiscordClient
     */
    public UIHandler(IDiscordClient cli)
    {
        activeUpdate = true;

        this.cli = cli;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // makes it look better :)
        } catch (@SuppressWarnings("all") Exception e) { // it needs to shut the fuck up
            e.printStackTrace();
        }

        // Begins the main frame
        beginMainFrame();

        // UI Updater (updates stats)
        Chad.runThread(() -> {
            while (activeUpdate)
            {
                try {
                    TimeUnit.MINUTES.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                update();
            }
        }, Chad.getInternalConsumer());
    }

    // Adds a log to the UI
    public final void addLog(String log, LogLevel level)
    {
        mainpanel.logs.append('\n' +"["+ level +"] "+ log);
    }

    /**
     * Creates a popup error
     * @param error The error string
     */
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

    /**
     * Creates a popup error with a guild's information
     * @param error The error string
     * @param guild The guild where the error occurred
     */
    static void newError(String error, IGuild guild)
    {
        PopUpPanel panel = new PopUpPanel();
        JFrame frame = new JFrame("Error : " + guild.getStringID());
        frame.getContentPane().add(panel);
        frame.setVisible(true);
        panel.errorContent.setEditable(false);
        panel.errorContent.setText(error);
        frame.setSize(488, 239);
        panel.guildButton.addActionListener((ev) -> displayGuild(guild));
        panel.exitButton.addActionListener((ev) -> frame.dispose());
    }

    /**
     * Creates a popup with the guild's information
     * @param guild The guild to be displayed
     */
    public static void displayGuild(IGuild guild)
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

    /**
     * Begins the main UI
     */
    private void beginMainFrame()
    {
        // The echo guilds command
        mainpanel.getAllGuilds.addActionListener((ev) -> {
          i = 0;
          ChadBot.cli.getGuilds().forEach((g) -> {
              // Counts the amount of guilds parsed
              add();
              // Sends the log
              addLog("<" + i + "> "+g.getName()+" ["+g.getStringID()+ ']', LogLevel.INFO);
          });
        });

        // Sets the exit button
        mainpanel.exitButton.addActionListener((ev) -> System.exit(0));

        // Sets the refresh button
        mainpanel.refreshButton.addActionListener((ev) -> update());

        // Caches all the guilds
        mainpanel.refreshButton2.addActionListener((ev) -> RequestBuffer.request(() -> ChadBot.cli.getGuilds().forEach((guild) -> Chad.getGuild(guild).cache())));

        // Gets the OperatingSystemMXBean
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
                java.lang.management.ManagementFactory.getOperatingSystemMXBean();

        // Gets memory amount
        String memory = Util.humanReadableByteCount(os.getTotalPhysicalMemorySize(), true);

        // Gets the available threads (it's not actually the physical cores)
        int availableProcessors = os.getAvailableProcessors();

        // Gets the ping to the first shard
        long ping = ChadBot.cli.getShards().get(0).getResponseTime();

        // Goes to a guild
        mainpanel.guildGo.addActionListener((ev) ->
        {
            try{
                // Displays the guild with the text from the input
                displayGuild(ChadBot.cli.getGuildByID(Long.parseLong(mainpanel.guildList.getText().trim())));
            } catch (NumberFormatException e)
            {
                // If it's invalid, eerror
                newError("Invalid Guild");
            }
        });

        // Set the cores value
        mainpanel.coresVal.setText(Integer.toString(availableProcessors));

        // Sets the memory value
        mainpanel.memoryVal.setText(memory);

        // Sets the shard response value
        mainpanel.shardRespTimeVal.setText(ping + "ms");

        // Sets the presence value
        mainpanel.presenceVal.setText("Loading");

        // So the main logs can't be edited
        mainpanel.logs.setEditable(false);

        // The startup message
        mainpanel.logs.setText("UI has started.");

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(1157, 842);
    }

    /**
     * Gets the statistics for the main UI
     * @param cli the IDiscordClient
     * @return a hashmap full of statistics
     */
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

    /**
     * Updates the UI
     */
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
        mainpanel.botToUserVal.setText(getStats(cli).get("botToPlayer"));
        mainpanel.threadVal.setText(String.valueOf(Chad.threadHash.size()));
        if (ChadBot.cli.isReady() && ChadBot.cli.getOurUser().getPresence().getText().isPresent()) {
            mainpanel.presenceVal.setText(ChadBot.cli.getOurUser().getPresence().getText().get());
        }
        mainpanel.shardRespTimeVal.setText(ping + "ms");
        mainpanel.coresVal.setText(Integer.toString(availableProcessors));
        mainpanel.memoryVal.setText(memory);
    }

    /**
     * Log Levels
     */
    public enum LogLevel
    {
        INFO, WARNING, SEVERE, EXCEPTION, CACHING
    }

    /**
     * Local Method, for lamdba
     */
    private void add()
    {
        i++;
    }
}
