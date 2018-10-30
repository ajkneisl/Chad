package com.jhobot.handle.ui;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
public class StatPanel extends JPanel {
    public StatPanel() {
        JLabel label1 = new JLabel();
        JLabel label2 = new JLabel();
        JLabel label3 = new JLabel();
        JLabel label4 = new JLabel();
        JLabel label5 = new JLabel();
        JLabel label6 = new JLabel();
        label7 = new JLabel();
        label8 = new JLabel();
        label9 = new JLabel();
        label10 = new JLabel();
        button1 = new JButton();
        setLayout(null);

        //---- label1 ----
        label1.setText("Statistics");
        label1.setFont(new Font("Corbel", Font.PLAIN, 26));
        add(label1);
        label1.setBounds(10, 15, 110, 55);

        //---- label2 ----
        label2.setText("Total Guilds");
        label2.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(label2);
        label2.setBounds(new Rectangle(new Point(10, 75), label2.getPreferredSize()));

        //---- label3 ----
        label3.setText("Average User Count");
        label3.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(label3);
        label3.setBounds(10, 100, 145, 20);

        //---- label4 ----
        label4.setText("Biggest Guild");
        label4.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(label4);
        label4.setBounds(10, 125, 100, 20);

        //---- label5 ----
        label5.setText("Bot to Player Ratio");
        label5.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(label5);
        label5.setBounds(10, 150, 135, 20);

        //---- label6 ----
        label6.setFont(new Font("Tahoma", Font.PLAIN, 16));
        label6.setText("http://bot.shoganeko.me");
        add(label6);
        label6.setBounds(230, 235, 165, 20);

        //---- label7 ----
        label7.setText("guildAmount");
        label7.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(label7);
        label7.setBounds(new Rectangle(new Point(220, 75), label7.getPreferredSize()));

        //---- label8 ----
        label8.setText("avgUserCount");
        label8.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(label8);
        label8.setBounds(220, 100, 85, 20);

        //---- label9 ----
        label9.setText("biggestGuild");
        label9.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(label9);
        label9.setBounds(220, 125, 85, 20);

        //---- label10 ----
        label10.setText("bottoPlayer");
        label10.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(label10);
        label10.setBounds(220, 150, 85, 20);

        //---- button1 ----
        button1.setText("Refresh");
        button1.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(button1);
        button1.setBounds(20, 225, 90, 30);

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

    private JLabel label7;
    private JLabel label8;
    private JLabel label9;
    private JLabel label10;
    private JButton button1;

    public void setGuildAmount(int i)
    {
        this.label7.setText(Integer.toString(i));
    }

    public void setBiggestGuild(String g)
    {
        this.label9.setText(g);
    }

    public void setAvgGuildSize(int i)
    {
        this.label8.setText(Integer.toString(i));
    }

    public void setBotToPlayer(String s)
    {
       this.label10.setText(s);
    }

    public JButton getRefreshButton()
    {
        return this.button1;
    }
}
