package com.jhobot;

import com.jhobot.handle.Commands;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Listener
{
    @EventSubscriber
    public void messageRecieved(MessageReceivedEvent e)
    {
        Commands.call(e);
    }

    @EventSubscriber
    public void joinGuild(GuildCreateEvent e)
    {
        // do stuff
    }

    @EventSubscriber
    public void leaveGuild(GuildLeaveEvent e)
    {
        // do stuff
    }
}
