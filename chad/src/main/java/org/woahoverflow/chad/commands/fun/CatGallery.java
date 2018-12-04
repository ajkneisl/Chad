package org.woahoverflow.chad.commands.fun;

import java.security.SecureRandom;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.ui.UIHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * @author sho
 * @since 0.6.3 B2
 */
public class CatGallery implements Command.Class  {
    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());

            // Makes sure there's no argumments
            if (args.isEmpty())
            {
                // Gets the catpictures directory
                File[] files = new File(System.getenv("appdata") + "\\chad\\catpictures\\").listFiles();

                // If the directory is somehow null
                if (files == null)
                {
                    messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                    return;
                }

                // If the directory is empty
                if (files.length == 0)
                {
                    UIHandler.handle.addLog("Cat Pictures directory empty!", UIHandler.LogLevel.SEVERE);
                    messageHandler.sendError(MessageHandler.INTERNAL_EXCEPTION);
                    return;
                }

                // Sends a random picture from the folder
                messageHandler.sendFile(files[new SecureRandom().nextInt(files.length)]);
                return;
            }

            // TODO unless something else is planned for this class, argument checking should be removed in general
            messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("catgallery", "Gives you a random cat picture.");
        return Command.helpCommand(st, "Cat Gallery", e);
    }
}
