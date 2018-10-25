package com.jhobot.handle.commands;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public interface Command
{
    Runnable run(MessageReceivedEvent e, List<String> args);
    Runnable help(MessageReceivedEvent e, List<String> args);
    PermissionLevels level();
    Category category();
}
