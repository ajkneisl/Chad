package com.jhobot.handle.commands;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

@SuppressWarnings("unused")
public interface Command
{
    Runnable run(MessageReceivedEvent e, List<String> args);
    Runnable help(MessageReceivedEvent e, List<String> args);
}
