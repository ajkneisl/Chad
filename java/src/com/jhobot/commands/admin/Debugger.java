package com.jhobot.commands.admin;

import com.jhobot.core.ChadBot;
import com.jhobot.handle.Log;
import com.jhobot.handle.LogLevel;
import com.jhobot.handle.MessageHandler;
import com.jhobot.handle.Util;
import com.jhobot.handle.commands.Category;
import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import com.jhobot.handle.commands.PermissionLevels;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.HashMap;
import java.util.List;

public class Debugger implements Command {

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            String subcmd = args.get(0);

            switch (subcmd) {
                default: // subcmd is the domain
                    break;
                case "addlog":
                    String domain = args.get(1);
                    String logmsg = args.get(2);
                    LogLevel loglvl = LogLevel.valueOf(args.get(3).toUpperCase());
                    ChadBot.DEBUG_HANDLER.internalLog(domain, logmsg, loglvl);
                    return;
            }

            String domain = args.get(0);
            if (args.size() > 1) {
                LogLevel level = LogLevel.valueOf(args.get(1).toUpperCase());
                List<Log> logs = ChadBot.DEBUG_HANDLER.getLogs(domain, level);
                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Internal Log");
                b.withDesc(domain);
                StringBuilder sb = new StringBuilder();
                for (Log log : logs) {
                    sb.append("(" + log.level.toString() + ") " + log.message + "\n");
                }
                b.appendField("Logs", sb.toString(), false);
                b.withColor(60, 0, 70);
                b.withFooterText(Util.getTimeStamp());
                new MessageHandler(e.getChannel()).sendEmbed(b.build());
                return;
            }
            List<Log> logs = ChadBot.DEBUG_HANDLER.getLogs(domain);
            EmbedBuilder b = new EmbedBuilder();
            b.withTitle("Internal Log");
            b.withDesc(domain);
            StringBuilder sb = new StringBuilder();
            for (Log log : logs) {
                sb.append("(" + log.level.toString() + ") " + log.message + "\n");
            }
            b.appendField("Logs", sb.toString(), false);
            b.withColor(60, 0, 70);
            b.withFooterText(Util.getTimeStamp());
            new MessageHandler(e.getChannel()).sendEmbed(b.build());
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("debugger <domain> [level]", "Displays the internal logs for the specified domain.");
        return HelpHandler.helpCommand(st, "Debugger", e);
    }

    @Override
    public PermissionLevels level() {
        return PermissionLevels.SYSTEM_ADMINISTRATOR;
    }

    @Override
    public Category category() {
        return Category.ADMIN;
    }
}
