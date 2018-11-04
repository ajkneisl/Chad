package com.jhobot.handle.ui;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class PopUpPanel extends JPanel {
    public PopUpPanel() {
        initComponents();
    }


    private void initComponents() {
        chadLabel = new JLabel();
        errorText = new JLabel();
        Exit = new JButton();
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextArea();

        setLayout(null);

        //---- chadLabel ----
        chadLabel.setText("Chad");
        chadLabel.setFont(chadLabel.getFont().deriveFont(chadLabel.getFont().getSize() + 15f));
        add(chadLabel);
        chadLabel.setBounds(10, 10, 85, 45);

        //---- errorText ----
        errorText.setText("Uh oh, an error occurred!");
        errorText.setFont(errorText.getFont().deriveFont(errorText.getFont().getSize() + 4f));
        add(errorText);
        errorText.setBounds(new Rectangle(new Point(125, 25), errorText.getPreferredSize()));

        //---- Exit ----
        Exit.setText("Exit");
        add(Exit);
        Exit.setBounds(new Rectangle(new Point(340, 145), Exit.getPreferredSize()));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(textArea1);
        }
        add(scrollPane1);
        scrollPane1.setBounds(20, 60, 360, 80);

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

    public JLabel chadLabel;
    public JLabel errorText;
    public JButton Exit;
    public JScrollPane scrollPane1;
    public JTextArea textArea1;
}
