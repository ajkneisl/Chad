package org.woahoverflow.chad.handle.ui.panels;

import javax.swing.*;
import java.awt.*;
/*
 * Created by JFormDesigner on Thu Nov 01 20:40:26 CDT 2018
 */



/**
 * @author sho joie
 */
public class MainPanel extends JPanel {
    public MainPanel() {
        initComponents();
    }

    private void initComponents() {
        JLabel chadText = new JLabel();
        JLabel allGuildsText = new JLabel();
        allGuildsValue = new JLabel();
        JLabel biggestGuildText = new JLabel();
        JLabel botToUserText = new JLabel();
        biggestGuildValue = new JLabel();
        botToUserVal = new JLabel();
        JLabel logsText = new JLabel();
        JScrollPane scrollPane1 = new JScrollPane();
        logs = new JTextArea();
        RefreshButton = new JButton();
        exitButton = new JButton();
        JLabel adv = new JLabel();
        JLabel lastReCacheAllText = new JLabel();
        lastReCacheAllValue = new JLabel();
        RefreshButton2 = new JButton();
        guildGo = new JButton();
        JLabel guildText = new JLabel();
        JLabel presenceText = new JLabel();
        presenceVal = new JLabel();
        JLabel currentThreadsText = new JLabel();
        threadVal = new JLabel();
        JLabel coresText = new JLabel();
        coresVal = new JLabel();
        JLabel memoryText = new JLabel();
        memoryVal = new JLabel();
        JLabel shardRespTimeText = new JLabel();
        shardRespTimeVal = new JLabel();
        guildList = new JTextArea();
        getAllGuilds = new JButton();
        setLayout(null);
        getAllGuilds.setText("Echo Guilds");
        getAllGuilds.setFont(new Font("Open Sans", Font.PLAIN, 13));
        getAllGuilds.setBounds(325, 54, getAllGuilds.getPreferredSize().width, getAllGuilds.getPreferredSize().height);
        add(getAllGuilds);
        chadText.setText("Chad");
        chadText.setFont(new Font("Open Sans", Font.PLAIN, 48));
        add(chadText);
        chadText.setBounds(50, 45, chadText.getPreferredSize().width, chadText.getPreferredSize().height);
        allGuildsText.setText("Guilds :");
        allGuildsText.setEnabled(false);
        allGuildsText.setFont(new Font("Open Sans", Font.PLAIN, 22));
        add(allGuildsText);
        allGuildsText.setBounds(15, 190, allGuildsText.getPreferredSize().width, allGuildsText.getPreferredSize().height);
        allGuildsValue.setText("0");
        allGuildsValue.setEnabled(false);
        allGuildsValue.setFont(new Font("Open Sans", Font.PLAIN, 22));
        add(allGuildsValue);
        allGuildsValue.setBounds(95, 190, 70, 25);
        biggestGuildText.setText("Biggest Guild :");
        biggestGuildText.setEnabled(false);
        biggestGuildText.setFont(new Font("Open Sans", Font.PLAIN, 22));
        add(biggestGuildText);
        biggestGuildText.setBounds(15, 220, biggestGuildText.getPreferredSize().width, biggestGuildText.getPreferredSize().height);
        botToUserText.setText("Bot to User :");
        botToUserText.setEnabled(false);
        botToUserText.setFont(new Font("Open Sans", Font.PLAIN, 22));
        add(botToUserText);
        botToUserText.setBounds(15, 250, botToUserText.getPreferredSize().width, botToUserText.getPreferredSize().height);
        biggestGuildValue.setText("0");
        biggestGuildValue.setEnabled(false);
        biggestGuildValue.setFont(new Font("Open Sans", Font.PLAIN, 22));
        add(biggestGuildValue);
        biggestGuildValue.setBounds(165, 220, 100, biggestGuildValue.getPreferredSize().height);
        botToUserVal.setText("0");
        botToUserVal.setEnabled(false);
        botToUserVal.setFont(new Font("Open Sans", Font.PLAIN, 22));
        add(botToUserVal);
        botToUserVal.setBounds(150, 250, 70, 25);
        logsText.setText("Logs");
        logsText.setEnabled(false);
        logsText.setFont(new Font("Open Sans", Font.PLAIN, 30));
        add(logsText);
        logsText.setBounds(300, 130, 80, 45);
        guildList.setBounds(325, 30, 175, 20);
        guildList.setEditable(true);
        add(guildList);
        {
            scrollPane1.setViewportView(logs);
        }
        add(scrollPane1);
        scrollPane1.setBounds(300, 175, 750, 555);
        RefreshButton.setText("Refresh");
        add(RefreshButton);
        RefreshButton.setFont(new Font("Open Sans", Font.PLAIN, 13));
        RefreshButton.setBounds(10, 688,  RefreshButton.getPreferredSize().width, RefreshButton.getPreferredSize().height);
        exitButton.setText("Exit");
        exitButton.setFont(new Font("Open Sans", Font.PLAIN, 13));
        exitButton.setFont(exitButton.getFont().deriveFont(exitButton.getFont().getSize() + 14f));
        add(exitButton);
        exitButton.setBounds(10, 745, 113, 48);
        adv.setText("https://bot.shoganeko.me");
        adv.setFont(new Font("Open Sans", Font.PLAIN, 20));
        add(adv);
        adv.setBounds(new Rectangle(new Point(895, 5), adv.getPreferredSize()));
        lastReCacheAllText.setText("Last Cache :");
        lastReCacheAllText.setFont(new Font("Open Sans", Font.PLAIN, 20));
        add(lastReCacheAllText);
        lastReCacheAllText.setBounds(10, 640, lastReCacheAllText.getPreferredSize().width, lastReCacheAllText.getPreferredSize().height);
        lastReCacheAllValue.setText("00/00/0000 00:00");
        lastReCacheAllValue.setFont(new Font("Open Sans", Font.PLAIN, 15));
        add(lastReCacheAllValue);
        lastReCacheAllValue.setBounds(125, 645, lastReCacheAllValue.getPreferredSize().width, lastReCacheAllValue.getPreferredSize().height);
        RefreshButton2.setText("ReCache");
        RefreshButton2.setFont(new Font("Open Sans", Font.PLAIN, 13));
        add(RefreshButton2);
        RefreshButton2.setBounds(10, 715, 85, 30);
        guildGo.setText("Go");
        add(guildGo);
        guildGo.setBounds(new Rectangle(new Point(450, 55), guildGo.getPreferredSize()));
        guildText.setText("Guild Moderation");
        guildText.setFont(guildText.getFont().deriveFont(guildText.getFont().getSize() + 3f));
        add(guildText);
        guildText.setBounds(new Rectangle(new Point(350, 5), guildText.getPreferredSize()));
        presenceText.setText("Presence :");
        presenceText.setFont(new Font("Open Sans", Font.PLAIN, 20));
        add(presenceText);
        presenceText.setBounds(10, 603, presenceText.getPreferredSize().width, presenceText.getPreferredSize().height);
        presenceVal.setText("Loading...");
        presenceVal.setFont(new Font("Open Sans", Font.PLAIN, 15));
        add(presenceVal);
        presenceVal.setBounds(110, 605, 200, 25);
        currentThreadsText.setText("Threads :");
        currentThreadsText.setFont(new Font("Open Sans", Font.PLAIN, 20));
        add(currentThreadsText);
        currentThreadsText.setBounds(10, 565, 90, 25);
        threadVal.setText("0");
        threadVal.setFont(new Font("Open Sans", Font.PLAIN, 15));
        add(threadVal);
        threadVal.setBounds(105, 567, 105, 25);
        coresText.setText("Cores :");
        coresText.setFont(new Font("Open Sans", Font.PLAIN, 20));
        add(coresText);
        coresText.setBounds(10, 525, coresText.getPreferredSize().width, coresText.getPreferredSize().height);
        coresVal.setText("0");
        coresVal.setFont(new Font("Open Sans", Font.PLAIN, 15));
        add(coresVal);
        coresVal.setBounds(80, 527, 105, 25);
        memoryText.setText("Ram :");
        memoryText.setFont(new Font("Open Sans", Font.PLAIN, 20));
        add(memoryText);
        memoryText.setBounds(10, 485, memoryText.getPreferredSize().width, memoryText.getPreferredSize().height);
        memoryVal.setText("0");
        memoryVal.setFont(new Font("Open Sans", Font.PLAIN, 15));
        add(memoryVal);
        memoryVal.setBounds(70, 488, 105, 25);
        shardRespTimeText.setText("Shard Response Time :");
        shardRespTimeText.setFont(new Font("Open Sans", Font.PLAIN, 20));
        add(shardRespTimeText);
        shardRespTimeText.setBounds(10, 445, shardRespTimeText.getPreferredSize().width, shardRespTimeText.getPreferredSize().height);
        shardRespTimeVal.setText("0ms");
        shardRespTimeVal.setFont(new Font("Open Sans", Font.PLAIN, 15));
        add(shardRespTimeVal);
        shardRespTimeVal.setBounds(225, 448, 75, 25);
    }

    public JLabel allGuildsValue;
    public JLabel biggestGuildValue;
    public JLabel botToUserVal;
    public JTextArea logs;
    public JButton RefreshButton;
    public JButton exitButton;
    public JLabel lastReCacheAllValue;
    public JButton RefreshButton2;
    public JTextArea guildList;
    public JButton guildGo;
    public JLabel presenceVal;
    public JLabel threadVal;
    public JLabel coresVal;
    public JButton getAllGuilds;
    public JLabel memoryVal;
    public JLabel shardRespTimeVal;
}
