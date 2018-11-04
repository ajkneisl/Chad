package com.jhobot.commands.fun;

import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.commands.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CatGallery implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler m = new MessageHandler(e.getChannel());
            if (args.size() == 0)
            {
                File[] files = new File(System.getenv("appdata") + "\\chad\\catpictures\\").listFiles();
                if (files == null)
                {
                    m.sendError("An internal error has occurred!");
                    return;
                }
                m.sendFile(files[new Random().nextInt(files.length)]);
                return;
            }

            m.sendError("Invalid Arguments.");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("catgallery", "Gives you a random cat picture.");
        return HelpHandler.helpCommand(st, "Cat Gallery", e);
    }
}
