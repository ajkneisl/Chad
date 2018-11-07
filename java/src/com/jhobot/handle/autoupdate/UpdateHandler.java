package com.jhobot.handle.autoupdate;

import com.jhobot.core.ChadBot;
import com.jhobot.core.ChadVar;
import com.jhobot.handle.ui.ChadException;
import com.jhobot.handle.ui.panels.UpdatePanel;
import jdk.nashorn.internal.runtime.ParserException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

class UpdateHandler
{
    static void updateClient(Updater update)
    {
        update.frame.dispose();
        if (ChadVar.ALLOW_UNSTABLE)
        {
            try {
                String full_file = "chad-"+update.VER_JSON.getString("recent-unstable")+".jar";
                ChadVar.UI_HANDLER.addLog("Downloading new unstable version...");
                FileUtils.copyURLToFile(
                        new URL("https://github.com/woahoverflow/Chad-Repo/tree/master/releases/alpha/"+full_file),
                        new File(System.getenv("appdata") + "\\chad\\versions\\"+full_file)
                        );
                File uri = new File(UpdateHandler.class.getProtectionDomain().getCodeSource().getLocation().getFile());
                FileUtils.copyFile(new File(System.getenv("appdata") + "\\chad\\versions\\chad-"+full_file),  new File(uri.getParent() + "\\" + full_file));
                ChadVar.UI_HANDLER.addLog("Download complete.");
                ChadVar.UI_HANDLER.addLog("New file is located in " + uri.getParent() + " with the name "+full_file+".");
                ChadVar.UI_HANDLER.addLog("Closing in 10 seconds.");
                ChadBot.cli.logout();
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ChadVar.UI_HANDLER.addLog("Closing now.");
                System.exit(1);
            } catch (IOException e) {
                ChadException.error("Error downloading new version!", e);
                e.printStackTrace();
            }
        }
        else {
            try {
                String full_file = "chad-"+update.VER_JSON.getString("recent-stable")+".jar";
                ChadVar.UI_HANDLER.addLog("Downloading new version...");
                FileUtils.copyURLToFile(
                        new URL("https://github.com/woahoverflow/Chad-Repo/tree/master/releases/alpha/"+full_file),
                        new File(System.getenv("appdata") + "\\chad\\versions\\"+full_file)
                );
                File uri = new File(UpdateHandler.class.getProtectionDomain().getCodeSource().getLocation().getFile());
                FileUtils.copyFile(new File(System.getenv("appdata") + "\\chad\\versions\\chad-"+full_file),  new File(uri.getParent() + "\\" + full_file));
                ChadVar.UI_HANDLER.addLog("Download complete.");
                ChadVar.UI_HANDLER.addLog("New file is located in " + uri.getParent() + " with the name "+full_file+".");
                ChadVar.UI_HANDLER.addLog("Closing in 10 seconds.");
                ChadBot.cli.logout();
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ChadVar.UI_HANDLER.addLog("Closing now.");
                System.exit(1);
            } catch (IOException e) {
                ChadException.error("Error downloading new version!", e);
                e.printStackTrace();
            }
        }
    }
}
