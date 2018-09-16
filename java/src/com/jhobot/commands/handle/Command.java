package com.jhobot.commands.handle;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public interface Command
{
    public void onRequest(MessageReceivedEvent e, List<String> args);
    public void helpCommand(MessageReceivedEvent e);
    public boolean botHasPermission(MessageReceivedEvent e);
    public boolean userHasPermission(MessageReceivedEvent e);
}
