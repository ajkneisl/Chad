package com.jhobot.commands.music;

import com.jhobot.core.ChadVar;
import com.jhobot.handle.MusicHandler;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.ui.ChadException;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.List;

public class Play implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            IGuild guild = e.getGuild();
            IVoiceChannel vc = e.getAuthor().getVoiceStateForGuild(guild).getChannel();

            if (!vc.isConnected())
            {
                vc.join();
                ChadVar.UI_HANDLER.addLog("Joined the voice channel");
            }

            while (!vc.isConnected())
            {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            MusicHandler handler = ChadVar.musicHandlers.get(guild);

            // if the guild doesnt have a music handler, register one
            if (handler == null)
            {
                try {
                    handler = new MusicHandler(guild);
                    ChadVar.musicHandlers.put(guild, handler);
                } catch (Exception exe) {
                    exe.printStackTrace();
                    ChadException.error("A problem occurred while creating a new music handler: " + exe.getMessage());
                }
            }

            ChadVar.UI_HANDLER.addLog("about to play");

            handler.play(args.get(0)); // lookup and enqueue the track provided

            ChadVar.UI_HANDLER.addLog("played");
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return null;
    }
}
