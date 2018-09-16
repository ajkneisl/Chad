package com.jhobot.commands.fun;

import com.jhobot.commands.handle.Command;
import com.jhobot.commands.handle.Messages;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class TicTacToe implements Command {

    @Override
    public void onRequest(MessageReceivedEvent e, List<String> args) {
        if (e.getMessage().getMentions().isEmpty())
        {
            new Messages(e.getChannel()).sendMessage("You didn't mention anyone!");
            return;
        }


    }

    @Override
    public void helpCommand(MessageReceivedEvent e) {

    }

    @Override
    public boolean botHasPermission(MessageReceivedEvent e) {
        return true;
    }

    @Override
    public boolean userHasPermission(MessageReceivedEvent e) {

        return true;
    }
}
