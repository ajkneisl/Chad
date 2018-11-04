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
            try {
                IGuild guild = e.getGuild();
                IVoiceChannel vc = e.getAuthor().getVoiceStateForGuild(guild).getChannel();

                if (!vc.isConnected())
                {
                    vc.join();
                    System.out.println("Joined the voice channel");
                }

                while (!vc.isConnected())
                {
                    Thread.sleep(1000);
                }

                MusicHandler handler = ChadVar.musicHandlers.get(guild);
                System.out.println("Got the music handler");

                // if the guild doesnt have a music handler, register one
                if (handler == null)
                {
                    try {
                        ChadException.error("Object 'handler' is null. Creating a new instance 'musicHandler'.");
                        MusicHandler musicHandler = new MusicHandler(guild);
                        ChadException.error("Successfully created Object 'musicHandler'");
                        ChadVar.musicHandlers.put(guild, musicHandler);
                        ChadException.error("Successfully registered MusicHandler 'musicHandler'.");
                        handler = musicHandler;
                        ChadException.error("Successfully assigned 'musicHandler' to 'handler'.");
                    } catch (Exception exe) {
                        exe.printStackTrace();
                        ChadException.error("A problem occurred while creating a new music handler: " + exe.getMessage());
                    }
                }

                handler.play(args.get(0)); // lookup and enqueue the track provided
            } catch (Exception ex) {
                ex.printStackTrace();
                ChadException.error("Oops! Something went wrong while trying to play a song (" + args.get(0) + ")");
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        return null;
    }
}
