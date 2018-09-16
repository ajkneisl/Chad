package com.jhobot.commands.handle;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public interface Command
{
    public void onRequest(MessageReceivedEvent e, List<String> args);
    public void helpCommand(MessageReceivedEvent e);
    public void botHasPermission(MessageReceivedEvent e);
    public void userHasPermission(MessageReceivedEvent e);
}
