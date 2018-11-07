package com.jhobot.handle.ui.panels;

import java.awt.*;
import javax.swing.*;

public class UpdatePanel extends JPanel  {

    public UpdatePanel()
    {
        initComponents();
    }

    private void initComponents() {
        JLabel chadLabel = new JLabel();
        JLabel newVersionLabel = new JLabel();
        newVersion = new JLabel();
        updateButton = new JButton();
        cancelButton = new JButton();

        //======== this2 ========
        {
            setLayout(null);

            //---- chadLabel ----
            chadLabel.setText("Chad");
            chadLabel.setFont(chadLabel.getFont().deriveFont(chadLabel.getFont().getSize() + 25f));
            add(chadLabel);
            chadLabel.setBounds(80, 10, 85, 45);

            //---- newVersion ----
            newVersionLabel.setText("Updated Available");
            newVersionLabel.setFont(newVersionLabel.getFont().deriveFont(newVersionLabel.getFont().getSize() + 4f));
            add(newVersionLabel);
            newVersionLabel.setBounds(new Rectangle(new Point(65, 50), newVersionLabel.getPreferredSize()));

            //---- chad ----
            newVersion.setFont(newVersion.getFont().deriveFont(newVersion.getFont().getSize() + 4f));
            newVersion.setText("chad-vX.Y.Z");
            add(newVersion);
            newVersion.setBounds(65, 70, 115, 19);

            //---- button1 ----
            updateButton.setText("Update");
            add(updateButton);
            updateButton.setBounds(15, 120, 67, 23);

            //---- button2 ----
            cancelButton.setText("Cancel");
            add(cancelButton);
            cancelButton.setBounds(165, 120, 67, 23);

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
    }

    public JLabel newVersion;
    public JButton updateButton; // Updater
    public JButton cancelButton; // Cancel
}
