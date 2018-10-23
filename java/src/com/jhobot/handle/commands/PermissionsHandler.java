package com.jhobot.handle.commands;

import com.google.gson.JsonObject;
import com.jhobot.core.JhoBot;
import sx.blah.discord.handle.obj.IUser;

import java.io.File;
import java.io.IOException;

public class PermissionsHandler {
    public PermissionsHandler() {
        File permissionsFile = new File(System.getenv("appdata") + "\\jho\\permissions.json");
        if (!permissionsFile.exists()) {
            try {
                System.out.println("Created permissions file");
                permissionsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasPermission(IUser user, PermissionLevels level) {
        String val = JhoBot.JSON_HANDLER.readFile("permissions.json").getString(Long.toString(user.getLongID()));
        if (val == level.toString() || val == null)
            return true;
        return false;
    }

    public PermissionLevels getLevel(IUser user) {
        String val = JhoBot.JSON_HANDLER.readFile("permissions.json").getString(Long.toString(user.getLongID()));
        return PermissionLevels.valueOf(val);
    }

    public void setPermission(IUser user, PermissionLevels level) {
        try {
            JhoBot.JSON_HANDLER.writeFile("permissions.json", Long.toString(user.getLongID()), level.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
