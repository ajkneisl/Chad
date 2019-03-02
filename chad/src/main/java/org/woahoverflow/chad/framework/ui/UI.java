package org.woahoverflow.chad.framework.ui;

import org.woahoverflow.chad.core.ChadInstance;
import org.woahoverflow.chad.framework.handle.ArgumentHandlerKt;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.handle.ThreadHandlerKt;
import org.woahoverflow.chad.framework.ui.panels.GuildPanel;
import org.woahoverflow.chad.framework.ui.panels.MainPanel;
import org.woahoverflow.chad.framework.ui.panels.PopUpPanel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The UI Handler
 *
 * @author sho
 */
public class UI {
    /**
     * The handle of this class
     */
    public static UI handle;

    /**
     * Local Integer, used in some Lambda expressions
     */
    private int i;

    /**
     * The Main Frame of the UI
     */
    private JFrame mainFrame;

    /**
     * The Main Panel of the UI
     */
    private final MainPanel mainpanel = new MainPanel();


    /**
     * Main Constructor for the UI
     */
    public UI() {
        if (ArgumentHandlerKt.isToggled("DISABLE_UI")) {
            ChadInstance.getLogger().warn("UI has been disabled!");
            return;
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // makes it look better :)
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame = new JFrame("Chad");

        // Begins the main frame
        beginMainFrame();

        // UI Updater (updates stats)
        new Thread(() -> {
            if (ArgumentHandlerKt.isToggled("DISABLE_UI")) {
                ChadInstance.getLogger().warn("UI Updating has been disabled!");
                return;
            }

            while (ArgumentHandlerKt.isToggled("DISABLE_UI_UPDATE")) {
                try {
                    TimeUnit.MINUTES.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                update();
            }
        }).start();
    }

    /**
     * Adds a log to the UI
     *
     * @param log The log string
     * @param level The log level
     */
    public final void addLog(String log, LogLevel level) {
        if (ArgumentHandlerKt.isToggled("DISABLE_UI")) {
            if (level.equals(LogLevel.EXCEPTION)) {
                ChadInstance.getLogger().error(log);
                return;
            }

            if (level.equals(LogLevel.INFO)) {
                ChadInstance.getLogger().info(log);
                return;
            }

            if (level.equals(LogLevel.WARNING)) {
                ChadInstance.getLogger().warn(log);
                return;
            }

            ChadInstance.getLogger().info(log);
            return;
        }

        mainpanel.logs.append('\n' +"["+ level +"] "+ log);
    }

    /**
     * Creates a popup error
     *
     * @param error The error string
     */
    static void newError(String error) {
        if (ArgumentHandlerKt.isToggled("DISABLE_UI")) {
            ChadInstance.getLogger().error(error);
            return;
        }

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
     *
     * @param error The error string
     * @param guild The guild where the error occurred
     */
    static void newError(String error, IGuild guild) {
        if (ArgumentHandlerKt.isToggled("DISABLE_UI")) {
            ChadInstance.getLogger().error("Error in Guild {}: {}", guild.getStringID(), error);
            return;
        }

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
     *
     * @param guild The guild to be displayed
     */
    public static void displayGuild(IGuild guild) {
        if (ArgumentHandlerKt.isToggled("DISABLE_UI")) {
            ChadInstance.getLogger().error("UI is denied, cannot display guild!");
            return;
        }

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

        if (!guild.getClient().getOurUser().getPermissionsForGuild(guild).contains(Permissions.CREATE_INVITE)) {
            panel.inviteLinkVal.setText("Bot doesn't have permission");
        }
        else {
            // creates an invite (watch out,it can spam it)
            String invite = RequestBuffer.request(() -> guild.getClient().getOurUser().getPermissionsForGuild(guild)
                .contains(Permissions.CREATE_INVITE)).get() ? "https://discord.gg/" + RequestBuffer.request(() -> guild.getDefaultChannel().createInvite(60, 100, false, true).getCode()).get()
                : "No Permission for Invite!";
            panel.inviteLinkVal.setText(invite);
        }

        panel.reCacheButton.addActionListener((ev) -> GuildHandler.handle.refreshGuild(guild.getLongID()));
    }

    /**
     * Begins the main UI
     */
    private void beginMainFrame() {
        // Frame's default values
        mainFrame.getContentPane().add(mainpanel);
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(1157, 842);

        // The echo guilds command
        mainpanel.getAllGuilds.addActionListener((ev) -> {
          i = 0;
          ChadInstance.cli.getGuilds().forEach((g) -> {
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

        // Gets the OperatingSystemMXBean
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
                java.lang.management.ManagementFactory.getOperatingSystemMXBean();

        // Gets memory amount
        String memory = os.getTotalPhysicalMemorySize()/1000/1000+"mb";

        // Gets the available threads (it's not actually the physical cores)
        int availableProcessors = os.getAvailableProcessors();

        // Gets the ping to the first shard
        long ping = ChadInstance.cli.getShards().get(0).getResponseTime();

        // Goes to a guild
        mainpanel.guildGo.addActionListener((ev) -> {
            try{
                // Displays the guild with the text from the input
                displayGuild(ChadInstance.cli.getGuildByID(Long.parseLong(mainpanel.guildList.getText().trim())));
            } catch (NumberFormatException e)
            {
                // If it's invalid, error
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
        mainpanel.presenceVal.setText("Loading...");

        // So the main logs can't be edited
        mainpanel.logs.setEditable(false);

        // The startup message
        mainpanel.logs.setText("UI has started.");
    }

    /**
     * Gets the statistics for the main UI
     *
     * @return A hashmap full of statistics
     */
    private static HashMap<String, String> getStats() {
        List<IGuild> guilds = RequestBuffer.request(ChadInstance.cli::getGuilds).get();

        int bots = 0;
        int users = 0;
        int biggestGuildAmount = 0;
        String biggestGuildName = "";
        for (IGuild g : guilds) {
            if (g.getUsers().size() > biggestGuildAmount) {
                biggestGuildName = g.getName();
                biggestGuildAmount = g.getUsers().size();
            }
            for (IUser u : g.getUsers()) {
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
        // Gets the statistics
        HashMap<String, String> stats = getStats();

        // Updates the UI with the given statistics
        mainpanel.allGuildsValue.setText(stats.get("guildAmount"));
        mainpanel.biggestGuildValue.setText(stats.get("biggestGuild"));
        mainpanel.botToUserVal.setText(stats.get("botToPlayer"));

        // The Thread hashmap's size
        mainpanel.threadVal.setText(String.valueOf(ThreadHandlerKt.getThreadHash().size()));

        // If the bot is ready and the presence text is present, update the value
        if (ChadInstance.cli.isReady() && ChadInstance.cli.getOurUser().getPresence().getText().isPresent()) {
            mainpanel.presenceVal.setText(ChadInstance.cli.getOurUser().getPresence().getText().get());
        }

        // Gets the OperatingSystemMXBean
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
            java.lang.management.ManagementFactory.getOperatingSystemMXBean();

        // Update values given fromt he OperatingSystemMXBean
        mainpanel.shardRespTimeVal.setText(ChadInstance.cli.getShards().get(0).getResponseTime() + "ms");
        mainpanel.coresVal.setText(Integer.toString(os.getAvailableProcessors()));
        mainpanel.memoryVal.setText(os.getTotalPhysicalMemorySize()/1000/1000+"mb");
    }

    /**
     * Log Levels
     */
    public enum LogLevel {
        INFO, WARNING, SEVERE, EXCEPTION
    }

    /**
     * Local Method, for lamdba
     */
    private void add()
    {
        i++;
    }
}
