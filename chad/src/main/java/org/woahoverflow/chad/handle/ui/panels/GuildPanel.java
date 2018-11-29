package org.woahoverflow.chad.handle.ui.panels;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GuildPanel extends JPanel {

    public GuildPanel()
    {
        initComponents();
    }

    private void initComponents() {
        JLabel chadLabel = new JLabel();
        JLabel guilnameText = new JLabel();
        guildNameVal = new JLabel();
        JLabel inviteLinkText = new JLabel();
        inviteLinkVal = new JLabel();
        reCacheButton = new JButton();
        leaveButton = new JButton();
        exitButton = new JButton();
        {
            setLayout(null);
            chadLabel.setText("Chad");
            chadLabel.setFont(chadLabel.getFont().deriveFont(chadLabel.getFont().getSize() + 25f));
            add(chadLabel);
            chadLabel.setBounds(10, 10, 85, 45);
            guilnameText.setText("Guild : ");
            guilnameText.setFont(guilnameText.getFont().deriveFont(guilnameText.getFont().getSize() + 4f));
            add(guilnameText);
            guilnameText.setBounds(new Rectangle(new Point(125, 25), guilnameText.getPreferredSize()));
            guildNameVal.setText("guildName");
            guildNameVal.setFont(guildNameVal.getFont().deriveFont(guildNameVal.getFont().getSize() + 4f));
            add(guildNameVal);
            guildNameVal.setBounds(175, 25, 210, 19);
            inviteLinkText.setText("Invite Link :");
            inviteLinkText.setFont(inviteLinkText.getFont().deriveFont(inviteLinkText.getFont().getSize() + 4f));
            add(inviteLinkText);
            inviteLinkText.setBounds(15, 65, 85, inviteLinkText.getPreferredSize().height);
            inviteLinkVal.setText("inviteLink");
            inviteLinkVal.setFont(inviteLinkVal.getFont().deriveFont(inviteLinkVal.getFont().getSize() + 4f));
            add(inviteLinkVal);
            inviteLinkVal.setBounds(105, 65, 300, 19);
            reCacheButton.setText("ReCache");
            add(reCacheButton);
            reCacheButton.setBounds(new Rectangle(new Point(405, 30), reCacheButton.getPreferredSize()));
            leaveButton.setText("Leave");
            add(leaveButton);
            leaveButton.setBounds(405, 60, 75, 23);
            exitButton.setText("Exit");
            add(exitButton);
            exitButton.setBounds(405, 90, 75, 23);
            {
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
    public JLabel guildNameVal;
    public JLabel inviteLinkVal;
    public JButton reCacheButton;
    public JButton leaveButton;
    public JButton exitButton;
}
