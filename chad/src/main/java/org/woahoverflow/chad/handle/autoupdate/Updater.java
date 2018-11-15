package org.woahoverflow.chad.handle.autoupdate;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.logging.LogLevel;
import org.woahoverflow.chad.handle.ui.panels.UpdatePanel;
import org.json.JSONObject;

import javax.swing.*;

public class Updater
{
    final JSONObject VER_JSON;
    JFrame frame;

    public Updater()
    {
        ChadVar.UI_HANDLER.addLog("Checking for updates...", LogLevel.UPDATE);
        this.VER_JSON = ChadVar.JSON_HANDLER.read("https://raw.githubusercontent.com/woahoverflow/Chad-Repo/master/data/version.json");
        boolean UPDATE = true;
        if (!ChadVar.ALLOW_UNSTABLE && ChadVar.VERSION.equals(this.VER_JSON.getString("recent-stable")))
            UPDATE = false;
        else if (ChadVar.ALLOW_UNSTABLE && ChadVar.VERSION.equals(this.VER_JSON.getString("recent-unstable")))
            UPDATE = false;

        if (!UPDATE)
        {
            ChadVar.UI_HANDLER.addLog("Client is up to date!", LogLevel.UPDATE);
            return;
        }

        ChadVar.UI_HANDLER.addLog("Found Update!", LogLevel.UPDATE);
        frame = new JFrame("Update Available!");
        UpdatePanel p = new UpdatePanel();
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
        ChadVar.UI_HANDLER.addLog("Exited Updater", LogLevel.UPDATE);
        frame.dispose();
    }
}
