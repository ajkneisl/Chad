package com.jhobot.handle.ui;

import java.awt.*;
import javax.swing.*;

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
        button1 = new JButton();
        JLabel adv = new JLabel();

        setLayout(null);

        //---- chadText ----
        chadText.setText("Chad");
        chadText.setFont(chadText.getFont().deriveFont(chadText.getFont().getSize() + 32f));
        add(chadText);
        chadText.setBounds(50, 45, 110, 50);

        //---- allGuildsText ----
        allGuildsText.setText("Guilds :");
        allGuildsText.setEnabled(false);
        allGuildsText.setFont(allGuildsText.getFont().deriveFont(allGuildsText.getFont().getSize() + 8f));
        add(allGuildsText);
        allGuildsText.setBounds(15, 190, 80, 25);

        //---- allGuildsValue ----
        allGuildsValue.setText("0");
        allGuildsValue.setEnabled(false);
        allGuildsValue.setFont(allGuildsValue.getFont().deriveFont(allGuildsValue.getFont().getSize() + 8f));
        add(allGuildsValue);
        allGuildsValue.setBounds(95, 190, 70, 25);

        //---- biggestGuildText ----
        biggestGuildText.setText("Biggest Guild :");
        biggestGuildText.setEnabled(false);
        biggestGuildText.setFont(biggestGuildText.getFont().deriveFont(biggestGuildText.getFont().getSize() + 8f));
        add(biggestGuildText);
        biggestGuildText.setBounds(15, 220, 145, 25);

        //---- botToUserText ----
        botToUserText.setText("Bot to User :");
        botToUserText.setEnabled(false);
        botToUserText.setFont(botToUserText.getFont().deriveFont(botToUserText.getFont().getSize() + 8f));
        add(botToUserText);
        botToUserText.setBounds(15, 250, 130, 25);

        //---- biggestGuildValue ----
        biggestGuildValue.setText("0");
        biggestGuildValue.setEnabled(false);
        biggestGuildValue.setFont(biggestGuildValue.getFont().deriveFont(biggestGuildValue.getFont().getSize() + 8f));
        add(biggestGuildValue);
        biggestGuildValue.setBounds(165, 220, 100, 25);

        //---- botToUserVal ----
        botToUserVal.setText("0");
        botToUserVal.setEnabled(false);
        botToUserVal.setFont(botToUserVal.getFont().deriveFont(botToUserVal.getFont().getSize() + 8f));
        add(botToUserVal);
        botToUserVal.setBounds(150, 250, 70, 25);

        //---- logsText ----
        logsText.setText("Logs");
        logsText.setEnabled(false);
        logsText.setFont(logsText.getFont().deriveFont(logsText.getFont().getSize() + 10f));
        add(logsText);
        logsText.setBounds(300, 130, 80, 45);

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(logs);
        }
        add(scrollPane1);
        scrollPane1.setBounds(300, 175, 750, 555);

        //---- RefreshButton ----
        RefreshButton.setText("Refresh");
        add(RefreshButton);
        RefreshButton.setBounds(15, 290, 90, 30);

        //---- button1 ----
        button1.setText("Exit");
        button1.setFont(button1.getFont().deriveFont(button1.getFont().getSize() + 14f));
        add(button1);
        button1.setBounds(10, 745, 113, 48);

        //---- adv ----
        adv.setText("https://bot.shoganeko.me");
        adv.setFont(adv.getFont().deriveFont(adv.getFont().getSize() + 8f));
        add(adv);
        adv.setBounds(new Rectangle(new Point(895, 5), adv.getPreferredSize()));

        { // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < getComponentCount(); i++) {
                Rectangle bounds = getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            setMinimumSize(preferredSize);
            setPreferredSize(preferredSize);
        }
    }

    JLabel allGuildsValue;
    JLabel biggestGuildValue;
    JLabel botToUserVal;
    JTextArea logs;
    JButton RefreshButton;
    JButton button1;
}
