package org.woahoverflow.chad.handle.autoupdate;

import org.woahoverflow.chad.core.ChadBot;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.logging.LogLevel;
import org.woahoverflow.chad.handle.ui.ChadException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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
                ChadVar.UI_HANDLER.addLog("Downloading new unstable version...", LogLevel.UPDATE);
                FileUtils.copyURLToFile(
                        new URL("https://github.com/woahoverflow/Chad-Repo/tree/master/releases/alpha/"+full_file),
                        new File(System.getenv("appdata") + "\\chad\\versions\\"+full_file)
                        );
                File uri = new File(UpdateHandler.class.getProtectionDomain().getCodeSource().getLocation().getFile());
                FileUtils.copyFile(new File(System.getenv("appdata") + "\\chad\\versions\\"+full_file),  new File(uri.getParent() + "\\" + full_file));
                ChadVar.UI_HANDLER.addLog("Download complete.", LogLevel.UPDATE);
                ChadVar.UI_HANDLER.addLog("New file is located in " + uri.getParent() + " with the name "+full_file+".", LogLevel.UPDATE);
                ChadVar.UI_HANDLER.addLog("Closing in 10 seconds.", LogLevel.WARNING);
                ChadBot.cli.logout();
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ChadVar.UI_HANDLER.addLog("Closing now.", LogLevel.WARNING);
                System.exit(1);
            } catch (IOException e) {
                ChadException.error("Error downloading new version!", e);
                e.printStackTrace();
            }
        }
        else {
            try {
                String full_file = "chad-"+update.VER_JSON.getString("recent-stable")+".jar";
                ChadVar.UI_HANDLER.addLog("Downloading new version...", LogLevel.UPDATE);
                FileUtils.copyURLToFile(
                        new URL("https://github.com/woahoverflow/Chad-Repo/tree/master/releases/final/"+full_file),
                        new File(System.getenv("appdata") + "\\chad\\versions\\"+full_file)
                );
                File uri = new File(UpdateHandler.class.getProtectionDomain().getCodeSource().getLocation().getFile());
                FileUtils.copyFile(new File(System.getenv("appdata") + "\\chad\\versions\\"+full_file),  new File(uri.getParent() + "\\" + full_file));
                ChadVar.UI_HANDLER.addLog("Download complete.", LogLevel.UPDATE);
                ChadVar.UI_HANDLER.addLog("New file is located in " + uri.getParent() + " with the name "+full_file+".", LogLevel.INFO);
                ChadVar.UI_HANDLER.addLog("Closing in 10 seconds.", LogLevel.WARNING);
                ChadBot.cli.logout();
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ChadVar.UI_HANDLER.addLog("Closing now.", LogLevel.WARNING);
                System.exit(1);
            } catch (IOException e) {
                ChadException.error("Error downloading new version!", e);
                e.printStackTrace();
            }
        }
    }
}
