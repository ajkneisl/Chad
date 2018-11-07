package com.jhobot.handle.ui.panels;

import java.awt.*;
import javax.swing.*;

public class PopUpPanel extends JPanel {
    public PopUpPanel() {
        initComponents();
    }

    private void initComponents() {
        JLabel chadLabel = new JLabel();
        JLabel errorText = new JLabel();
        exitButton = new JButton();
        JScrollPane scrollPane1 = new JScrollPane();
        errorContent = new JTextArea();
        guildButton = new JButton();

        //======== this ========

        setLayout(null);

        //---- chadLabel ----
        chadLabel.setText("Chad");
        chadLabel.setFont(chadLabel.getFont().deriveFont(chadLabel.getFont().getSize() + 25f));
        add(chadLabel);
        chadLabel.setBounds(10, 10, 85, 45);

        //---- errorText ----
        errorText.setText("Uh oh, an error occurred!");
        errorText.setFont(errorText.getFont().deriveFont(errorText.getFont().getSize() + 4f));
        add(errorText);
        errorText.setBounds(new Rectangle(new Point(125, 25), errorText.getPreferredSize()));

        //---- Exit ----
        exitButton.setText("Exit");
        add(exitButton);
        exitButton.setBounds(410, 165, 55, 20);

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(errorContent);
        }
        add(scrollPane1);
        scrollPane1.setBounds(20, 60, 370, 130);

        //---- button1 ----
        guildButton.setText("Guild");
        add(guildButton);
        guildButton.setBounds(410, 130, 55, 20);

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

    public JButton exitButton;
    public JTextArea errorContent;
    public JButton guildButton;
}
