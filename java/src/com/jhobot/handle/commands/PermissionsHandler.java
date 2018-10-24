package com.jhobot.handle.commands;

import com.jhobot.core.JhoBot;
import sx.blah.discord.handle.obj.IUser;

public class PermissionsHandler {
    public PermissionsHandler() {}

    public boolean hasPermission(IUser user, PermissionLevels level) {
        if (user.getLongID() == Long.parseLong("173495550467899402"))
            return true;
        PermissionLevels val = JhoBot.DATABASE_HANDLER.getPermissionLevel(user);
        if (val == level || val == null)
            return true;
        return false;
    }

    public PermissionLevels getLevel(IUser user) {
        if (user.getStringID() == "173495550467899402")
            return PermissionLevels.SYSTEM_ADMINISTRATOR;
        String val = JhoBot.JSON_HANDLER.readFile("permissions.json").getString(Long.toString(user.getLongID()));
        return PermissionLevels.valueOf(val);
    }

    public void setPermission(IUser user, PermissionLevels level) {
        JhoBot.DATABASE_HANDLER.setPermissionLevel(user, level);
    }
}
