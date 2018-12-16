package org.woahoverflow.chad.commands.admin;

import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.obj.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author codebasepw
 * @since 0.7.0
 */
public class TestCommands implements Command.Class {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            for (Command.Data data : ChadVar.COMMANDS.values())
            {
                messageHandler.sendError(String.format("Processing %s", data.getCommandAliases()[0]));

                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException ex) { ex.printStackTrace(); }

                switch (data.getCommandAliases()[0])
                {
                    default:
                        messageHandler.sendError(String.format("Could not process `%s`", data.getCommandAliases()[0]));
                        return;
                    case "createplayer": // c!createplayer 100 100 100
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("100"); add("100"); add("100"); }}), Chad.getInternalConsumer());
                        break;
                    case "threads": // c!threads
                        data.getCommandClass().run(e, new ArrayList<String>() {{ }});
                        break;
                    case "modpresence": // c!modpresence string - c!modpresence status online/idle/offline/dnd
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("TESTING"); }}), Chad.getInternalConsumer()); // c!modpresence TESTING
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("status"); add("online"); }}), Chad.getInternalConsumer()); // c!modpresence status online
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("status"); add("idle"); }}), Chad.getInternalConsumer()); // c!modpresence status idle
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("status"); add("offline"); }}), Chad.getInternalConsumer()); // c!modpresence status offline
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("status"); add("dnd"); }}), Chad.getInternalConsumer()); // c!modpresence status dnd
                        break;
                    case "setbal": // c!setbal 666
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("666"); }}), Chad.getInternalConsumer());
                        break;
                    case "shutdown": // c!shutdown
                        messageHandler.sendError("Oh noes! Definitely shouldn't shut down the bot during testing.");
                        break;
                    case "systeminfo": // c!systeminfo
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ }}), Chad.getInternalConsumer());
                        break;
                    case "testcommands": // c!testcommands
                        messageHandler.sendError("Testing already in progress!");
                        break;
                    case "attack": // c!attack user
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add(e.getAuthor().mention()); }}), Chad.getInternalConsumer());
                        break;
                    case "respawn": // c!respawn
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ }}), Chad.getInternalConsumer());
                        break;
                    case "viewplayer": // c!viewplayer
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ }}), Chad.getInternalConsumer());
                        break;
                    case "catfact": // c!catfact
                        //TODO
                        break;
                    case "catgallery": // c!catgallery
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ }}), Chad.getInternalConsumer());
                        break;
                    case "cuddle": // c!cuddle
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add(e.getAuthor().mention()); }}), Chad.getInternalConsumer());
                        break;
                    case "divorce": // c!divorce
                        //TODO: not sure if i should do this one
                        //data.getCommandClass().run(e, new ArrayList<String>() {{ }});
                        break;
                    case "downvote": // c!eightball
                        //TODO: not sure if i should do this one either
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add(e.getAuthor().mention()); }}), Chad.getInternalConsumer());
                        break;
                    case "eightball": // c!eightball
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("Will Chad ever work properly?"); }}), Chad.getInternalConsumer());
                        break;
                    case "marry": // c!marry
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add(e.getAuthor().mention()); }}), Chad.getInternalConsumer());
                        break;
                    case "photoeditor": // c!photoeditor
                        //TODO: i cant be fucked to do this one
                        break;
                    case "profile":
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ }}), Chad.getInternalConsumer());
                        break;
                    case "random":
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("number"); }}), Chad.getInternalConsumer());
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("quote"); }}), Chad.getInternalConsumer());
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("word"); }}), Chad.getInternalConsumer());
                        break;
                    case "rockpaperscissors":
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("rock"); }}), Chad.getInternalConsumer());
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("paper"); }}), Chad.getInternalConsumer());
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("scissors"); }}), Chad.getInternalConsumer());
                        break;
                    case "russianroulette":
                        //TODO: i dont think you can play against chad
                        break;
                    case "upvote":
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add(e.getAuthor().mention()); }}), Chad.getInternalConsumer());
                        break;
                    case "wordreverse":
                        Chad.runThread(data.getCommandClass().run(e, new ArrayList<String>() {{ add("TESTING"); }}), Chad.getInternalConsumer());
                        break;
                }
            }
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        return null;
    }
}
