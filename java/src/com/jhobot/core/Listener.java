package com.jhobot.core;

import com.jhobot.commands.admin.*;
import com.jhobot.commands.fun.*;
import com.jhobot.commands.function.Logging;
import com.jhobot.commands.function.Message;
import com.jhobot.commands.function.Prefix;
import com.jhobot.commands.function.Purge;
import com.jhobot.commands.info.*;
import com.jhobot.commands.punishments.Ban;
import com.jhobot.commands.punishments.Kick;
import com.jhobot.handle.LogLevel;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.DatabaseHandler;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.PermissionsHandler;
import com.jhobot.handle.commands.ThreadCountHandler;
import com.jhobot.handle.commands.permissions.PermissionHandler;
import com.jhobot.handle.ui.UIHandler;
import com.sun.corba.se.impl.activation.CommandHandler;
import org.bson.Document;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.Future;

class Listener
{
    @SuppressWarnings({"unchecked", "LoopStatementThatDoesntLoop"})
    @EventSubscriber
    public void messageRecieved(MessageReceivedEvent e)
    {
        // Gets the message, then splits all the different parts with a space.
        String[] argArray = e.getMessage().getContent().split(" ");

        // Returns if there are no arguments
        if (argArray.length == 0)
            return;

        String prefix = ChadBot.DATABASE_HANDLER.getString(e.getGuild(), "prefix"); // to prevent multiple requests

        // If the prefix isn't jho! it returns
        if (!argArray[0].startsWith(prefix))
            return;

        // Gets the command string aka stuff after jho!
        String commandString = argArray[0].substring(prefix.length()).toLowerCase();

        // Gets the arguments & removes the command strings
        List<String> args = new ArrayList<>(Arrays.asList(argArray));
        args.remove(0);

        if (!ThreadCountHandler.HANDLER.allowThread(e.getAuthor()))
            return;

        // command runner
        HashMap<String, Command> hash = new HashMap<>();

        hash.put("userinfo", new UserInfo());
        hash.put("kick", new Kick());
        hash.put("ban", new Ban());
        hash.put("updatelog", new UpdateLog());
        hash.put("steam", new Steam());
        hash.put("chad", new Chad());
        hash.put("guildinfo", new GuildInfo());
        hash.put("prefix", new Prefix());
        hash.put("logging", new Logging());
        hash.put("random", new com.jhobot.commands.fun.Random());
        hash.put("pe", new PhotoEditor());
        hash.put("8ball", new EightBall());
        hash.put("catgallery", new CatGallery());
        hash.put("catfact", new CatFact());
        hash.put("help", new Help());
        hash.put("rrl", new RussianRoulette());
        hash.put("purge", new Purge());
        hash.put("im", new Message());
        hash.put("threads", new CurrentThreads()); // admin only/debug
        hash.put("rtop", new RedditTop());
        hash.put("rnew", new RedditNew());
        hash.put("systeminfo", new SystemInfo());
        hash.put("modpresence", new ModifyPresence()); // admin only
        hash.put("setlevel", new SetLevel());
        hash.put("getlevel", new GetLevel());
        hash.put("perms", new com.jhobot.commands.function.Permissions());
        hash.put("debugger", new Debugger());
        //System.out.println(e.getAuthor().getStringID() + " - " + Long.toString(e.getAuthor().getLongID()));
        hash.forEach((k, v) -> {
            if (commandString.equalsIgnoreCase(k))
            {
                ChadBot.DEBUG_HANDLER.internalLog("chad.internal.listener", "Running command: " + k, LogLevel.INFO);
                Future<?> thread;
                // if the user doesnt have the required permission level, deny them access to the command
                /*if (!PermissionHandler.HANDLER.userHasPermission(k, e.getAuthor(), e.getGuild()) && !e.getAuthor().getPermissionsForGuild(e.getGuild()).contains(Permissions.ADMINISTRATOR))
                {
                    new MessageHandler(e.getChannel()).sendError("You don't have permission for this!");
                }*/
                if (args.size() == 1 && args.get(0).equalsIgnoreCase("help"))
                    thread = ChadBot.EXECUTOR.submit(v.help(e, args));
                else
                    thread = ChadBot.EXECUTOR.submit(v.run(e, args));
                ThreadCountHandler.HANDLER.addThread(thread, e.getAuthor());
            }
        });
    }

    @EventSubscriber
    public void userJoin(UserJoinEvent e)
    {
        // for logging
        IGuild g = e.getGuild();
        MessageHandler m = new MessageHandler(null);
        EmbedBuilder b = new EmbedBuilder();
        Date date = Date.from(e.getGuild().getCreationDate());
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        b.withTitle("User Join : " + e.getUser().getName())
                .withFooterIcon(e.getUser().getAvatarURL())
                .withFooterText(Util.getTimeStamp())
                .appendField("Join Time", format.format(date), true);

        m.sendLog(b.build(), ChadBot.DATABASE_HANDLER, g);


        if (ChadBot.DATABASE_HANDLER.getBoolean(e.getGuild(), "join_msg_on"))
        {
            String joinMsgCh = ChadBot.DATABASE_HANDLER.getString(e.getGuild(), "join_message_ch");
            if (!joinMsgCh.equalsIgnoreCase("none")) {
                Long id = Long.parseLong(joinMsgCh);
                IChannel ch = RequestBuffer.request(() -> g.getChannelByID(id)).get();
                if (!ch.isDeleted())
                {
                    String msg = ChadBot.DATABASE_HANDLER.getString(e.getGuild(), "join_message");
                    msg = msg.replaceAll("&user&", e.getUser().getName()).replaceAll("&guild&", e.getGuild().getName());
                    new MessageHandler(ch).sendMessage(msg);
                }
            }
        }

    }

    @EventSubscriber
    public void userLeave(UserLeaveEvent e)
    {
        // for logging
        IGuild g = e.getGuild();
        MessageHandler m = new MessageHandler(null);
        EmbedBuilder b = new EmbedBuilder();
        Date date = Date.from(e.getGuild().getCreationDate());
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        b.withTitle("User Leave : " + e.getUser().getName())
                .withFooterIcon(e.getUser().getAvatarURL())
                .withFooterText(Util.getTimeStamp())
                .appendField("Leave Time", format.format(date), true);

        m.sendLog(b.build(), ChadBot.DATABASE_HANDLER, g);

        if (ChadBot.DATABASE_HANDLER.getBoolean(e.getGuild(), "leave_msg_on"))
        {
            String leaveMsgCh = ChadBot.DATABASE_HANDLER.getString(e.getGuild(), "leave_message_ch");
            if (!leaveMsgCh.equalsIgnoreCase("none"))
            {
                Long id = Long.parseLong(leaveMsgCh);
                IChannel ch = RequestBuffer.request(() -> g.getChannelByID(id)).get();
                if (!ch.isDeleted())
                {
                    String msg = ChadBot.DATABASE_HANDLER.getString(e.getGuild(), "leave_message");
                    msg = msg.replaceAll("&user&", e.getUser().getName()).replaceAll("&guild&", e.getGuild().getName());
                    new MessageHandler(ch).sendMessage(msg);
                }
            }
        }
    }

    @EventSubscriber
    public void joinGuild(GuildCreateEvent e)
    {
        DatabaseHandler dbb = ChadBot.DATABASE_HANDLER;
        if (!dbb.exists(e.getGuild()))
        {
            Document doc = new Document();

            doc.append("guildid", e.getGuild().getStringID());
            doc.append("prefix", "j!");
            if (!e.getClient().getOurUser().getPermissionsForGuild(e.getGuild()).contains(Permissions.MANAGE_ROLES))
                doc.append("muted_role", "none_np");
            else
                doc.append("muted_role", "none");
            doc.append("muted_role", "none");
            doc.append("logging", false);
            doc.append("logging_channel", "none");
            doc.append("cmd_requires_admin", false);
            doc.append("music_requires_admin", false);
            doc.append("role_on_join", false);
            doc.append("join_role", "none");
            doc.append("ban_message", "You have been banned from &guild&. \n &reason&");
            doc.append("kick_message", "You have been kicked from &guild&. \n &reason&");
            doc.append("allow_level_message", false);
            doc.append("allow_leveling", false);
            doc.append("join_message", "`&user&` has joined the guild!");
            doc.append("leave_message", "`&user&` has left the guild!");
            doc.append("join_msg_on", false);
            doc.append("leave_msg_on", false);
            doc.append("ban_msg_on", true);
            doc.append("kick_msg_on", true);
            doc.append("join_message_ch", "none");
            doc.append("leave_message_ch", "none");

            dbb.getCollection().insertOne(doc);

            System.out.println(Util.getTimeStamp() + " <" + e.getGuild().getStringID() + "> Joined Guild");
        }
    }

    @EventSubscriber
    public void leaveGuild(GuildLeaveEvent e)
    {
        DatabaseHandler databaseHandler = ChadBot.DATABASE_HANDLER;
        Document get = databaseHandler.getCollection().find(new Document("guildid", e.getGuild().getStringID())).first();

        if (get == null)
            return;

        databaseHandler.getCollection().deleteOne(get);

        System.out.println(Util.getTimeStamp() + " <" + e.getGuild().getStringID() + "> Left Guild");
    }



    @EventSubscriber
    public void onReadyEvent(ReadyEvent e)
    {
        // automatic presence updater
        ChadBot.EXECUTOR.submit(() -> {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    String[] ar = {"hello!", "gamers", "epic gamers", "a bad game", "j!help", "j!prefix set *", "what's going on gamers", "invite me please"};
                    e.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, ar[new Random().nextInt(ar.length)]);
                }
            }, 0, 60000*5);
        });

        // automatic ui updater
        ChadBot.EXECUTOR.submit(() -> {
            Timer t = new Timer();
            UIHandler ui = new UIHandler(e.getClient());
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    ui.update();
                }
            }, 0, 60000*5);
            ui.getPanel().getRefreshButton().addActionListener((ActionEvent) ->  ui.update());
        });
    }
}
