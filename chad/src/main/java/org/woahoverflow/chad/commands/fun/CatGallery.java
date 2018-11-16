package org.woahoverflow.chad.commands.fun;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.MessageHandler;
import org.woahoverflow.chad.handle.commands.Command;
import org.woahoverflow.chad.handle.ui.UIHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CatGallery implements Command.Class  {
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
                if (files.length == 0)
                {
                    ChadVar.UI_HANDLER.addLog("Cat Pictures directory empty!", UIHandler.LogLevel.SEVERE);
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
        return Command.helpCommand(st, "Cat Gallery", e);
    }
}
