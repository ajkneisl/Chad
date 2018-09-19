package com.jhobot.handle.commands;

import com.jhobot.handle.DB;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public interface CommandClass
{
    public void onRequest(MessageReceivedEvent e, List<String> args, DB db);
    public void helpCommand(MessageReceivedEvent e, DB db);
    public boolean botHasPermission(MessageReceivedEvent e, DB db);
    public boolean userHasPermission(MessageReceivedEvent e, DB db);
}
