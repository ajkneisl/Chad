package com.jhobot.handle.autoupdate;

import com.jhobot.core.ChadVar;
import com.jhobot.handle.Util;
import com.jhobot.handle.ui.panels.UpdatePanel;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Timer;
import java.util.TimerTask;

public class Updater
{
    boolean UPDATE = true;
    JSONObject VER_JSON;
    JFrame frame;
    UpdatePanel p;
    public Updater()
    {
        ChadVar.UI_HANDLER.addLog("Checking for updates...");
        this.VER_JSON = ChadVar.JSON_HANDLER.read("https://raw.githubusercontent.com/woahoverflow/Chad-Repo/master/data/ver.json");
        if (!ChadVar.ALLOW_UNSTABLE && ChadVar.VERSION.equals(this.VER_JSON.getString("recent-stable")))
            this.UPDATE = false;
        else if (ChadVar.ALLOW_UNSTABLE && ChadVar.VERSION.equals(this.VER_JSON.getString("recent-unstable")))
            this.UPDATE = false;

        if (!this.UPDATE)
        {
            ChadVar.UI_HANDLER.addLog("Client is up to date!");
            return;
        }

        ChadVar.UI_HANDLER.addLog("Found Update!");
        frame = new JFrame("Update Available!");
        p = new UpdatePanel();
        if (ChadVar.ALLOW_UNSTABLE)
            p.newVersion.setText(this.VER_JSON.getString("recent-unstable") + " (unstable)");
        else
            p.newVersion.setText(this.VER_JSON.getString("recent-stable"));
        p.updateButton.addActionListener((ActionEvent) -> UpdateHandler.updateClient(this));
        p.cancelButton.addActionListener((ActionEvent) -> exitUpdater());
        p.setVisible(true);
        frame.setVisible(true);
        frame.getContentPane().add(p);
        frame.setSize(265, 193);
    }

    private void exitUpdater()
    {
        ChadVar.UI_HANDLER.addLog("Exited Updater");
        frame.dispose();

    }
}
