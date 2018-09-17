package com.jhobot.commands.music;

import com.jhobot.handle.DB;
import com.jhobot.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class Play implements Command {
    @Override
    public void onRequest(MessageReceivedEvent e, List<String> args, DB db) {

    }

    @Override
    public void helpCommand(MessageReceivedEvent e, DB db) {

    }

    @Override
    public boolean botHasPermission(MessageReceivedEvent e, DB db) {
        return false;
    }

    @Override
    public boolean userHasPermission(MessageReceivedEvent e, DB db) {
        return false;
    }
}
