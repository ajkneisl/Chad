package com.jhobot.core;

import com.jhobot.handle.*;
import com.jhobot.handle.commands.*;
import com.jhobot.handle.commands.permissions.PermissionHandler;
import com.jhobot.handle.ui.UIHandler;
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
import java.util.concurrent.Future;

@SuppressWarnings("CanBeFinal")
public class Listener
{
    public static HashMap<String, Command> hash = new HashMap<>();
    public static HashMap<String, MetaData> metaData = new HashMap<>();

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
        // this is now statically defined
       // HashMap<String, Command> hash = new HashMap<>();
        //System.out.println(e.getAuthor().getStringID() + " - " + Long.toString(e.getAuthor().getLongID()));
        hash.forEach((k, v) -> {
            if (commandString.equalsIgnoreCase(k)) // comment for commit
            {
                Future<?> thread;

                MetaData meta = metaData.get(k);

                // if the command is system administrator only, and the user isnt a system administrator, deny them access
                if (meta.isDevOnly && !PermissionHandler.HANDLER.userIsDeveloper(e.getAuthor()))
                {
                    new MessageHandler(e.getChannel()).sendError("You don't have permission for this!");
                    return;
                }

                if (!PermissionHandler.HANDLER.userHasPermission(k, e.getAuthor(), e.getGuild()) && !e.getAuthor().getPermissionsForGuild(e.getGuild()).contains(Permissions.ADMINISTRATOR))
                {
                    new MessageHandler(e.getChannel()).sendError("You don't have permission for this!");
                    return;
                }
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

        // does the bot have MANAGE_ROLES?
        if (!ChadBot.cli.getOurUser().getPermissionsForGuild(e.getGuild()).contains(Permissions.MANAGE_ROLES)) {
            new MessageHandler(e.getGuild().getDefaultChannel()).sendError("Auto role assignment failed; Bot doesn't have permission: MANAGE_ROLES.");
            return;
        }

        // you probably shouldnt put code below this comment

        String joinRoleStringID = ChadBot.DATABASE_HANDLER.getString(e.getGuild(), "join_role");
        Long joinRoleID = Long.parseLong(joinRoleStringID);
        List<IRole> botRoles = ChadBot.cli.getOurUser().getRolesForGuild(e.getGuild());
        IRole joinRole = e.getGuild().getRoleByID(joinRoleID);

        // get the bots highest role position in the guild
        int botPosition = 0;
        for (IRole role : botRoles) {
            if (role.getPosition() > botPosition) {
                botPosition = role.getPosition();
            }
        }

        // can the bot assign the user the configured role?
        if (joinRole.getPosition() > botPosition) {
            new MessageHandler(e.getGuild().getDefaultChannel()).sendError("Auto role assignment failed; Bot isn't allowed to assign the role.");
            return;
        }

        // is the role @everyone?
        if (joinRole.isEveryoneRole()) {
            new MessageHandler(e.getGuild().getDefaultChannel()).sendError("Auto role assignment failed; Misconfigured role.");
            return;
        }

        // assign the role
        if (ChadBot.DATABASE_HANDLER.getBoolean(e.getGuild(), "role_on_join")) {
            if (joinRoleStringID != "none") {
                e.getUser().addRole(joinRole);
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

    public static int ROTATION_TIME = 60000*5; // 5 minutes
    public static boolean ROTATE_PRESENCE = true;
    public static List<String> PRESENCE_ROTATION = new ArrayList<>();

    static boolean ALLOWUI = false;

    @EventSubscriber
    public void onReadyEvent(ReadyEvent e)
    {
        // automatic presence updater
        //TODO: put this in its own thread class so i can change the timings on it
        ChadBot.EXECUTOR.submit(() -> {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!ROTATE_PRESENCE)
                        return;
                    Object[] ar = PRESENCE_ROTATION.toArray();
                    int rotation = ar.length;
                    e.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, (String)ar[new Random().nextInt(rotation)]);
                }
            }, 0, ROTATION_TIME); // this cant be changed for some reason, i would probably have to reschedule the timer in order for this to work
        });

        // UI Updater
        if (ALLOWUI)
        {
            UIHandler h = new UIHandler(e.getClient());
            ChadBot.EXECUTOR.submit(() -> {
                java.util.Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        h.update();
                    }
                }, 0, 60000*5);
                h.getPanel().getRefreshButton().addActionListener((ActionEvent) ->  h.update());
            });
        }
    }
}
