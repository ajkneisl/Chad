package com.jhobot.commands.admin;

import com.jhobot.core.ChadBot;
import com.jhobot.handle.commands.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public class Shutdown implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            ChadBot.cli.logout(); // logout

            System.exit(0); // initiate a system exit with status code 0
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return null;
    }
}
