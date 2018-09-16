package com.jhobot.commands.fun;

import com.jhobot.commands.handle.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class TicTacToe implements Command {

    @Override
    public void onRequest(MessageReceivedEvent e, List<String> args) {
        int toe = Integer.parseInt(args.get(0));
    }

    @Override
    public void helpCommand(MessageReceivedEvent e) {

    }

    @Override
    public void botHasPermission(MessageReceivedEvent e) {

    }

    @Override
    public void userHasPermission(MessageReceivedEvent e) {

    }
}
