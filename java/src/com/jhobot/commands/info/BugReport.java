package com.jhobot.commands.info;

import com.jhobot.handle.commands.Command;
import com.jhobot.handle.commands.HelpHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class BugReport implements Command {
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return null;/*() -> {
            Messages m = new Messages(e.getChannel());
            if (args.size() == 0)
            {
                help(e, args);
                return;
            }

            MongoCollection<Document> col = JhoBot.db.getSeperateCollection("bug_report").getCollection();

            if (args.size() == 1 && args.get(0).equalsIgnoreCase("read"))
            {
                boolean allow = false;
                for (Long l : JhoBot.allowedUsers())
                {
                    if (e.getAuthor().getLongID() == l)
                        allow = true;
                }
                if (!allow)
                {
                    m.sendError("You don't have permission for this!");
                    return;
                }

                Document get = col.find(new Document("obj", "main")).first();

                if (get == null)
                {
                    m.sendError("An internal error has occurred.");
                    return;
                }

                if (Integer.parseInt(get.getString("amount")) == 0)
                {
                    m.sendError("There are no bugs to review!");
                    return;
                }

                int f = Integer.parseInt(get.getString("amount"));
                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Bug Reviewer");
                b.withFooterText(Util.getTimeStamp() + " : " + JhoBot.db.getString(e.getGuild(), "prefix") + "bug read <num>");
                b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
                for (int i = 1; f >= i; i++)
                {
                    Document get2 = col.find(new Document("id", Integer.toString(i))).first();
                    if (get2 == null)
                    {
                        m.sendError("There was an internal exception.");
                        return;
                    }
                    b.appendField("ID : " + i, (String) get2.get("details"), false);
                }

                m.sendEmbed(b.build());
                return;
            }

            if (args.size() == 2 && args.get(0).equalsIgnoreCase("read"))
            {
                boolean allow = false;
                for (Long l : JhoBot.allowedUsers())
                {
                    if (e.getAuthor().getLongID() == l)
                        allow = true;
                }
                if (!allow)
                {
                    m.sendError("You don't have permission for this!");
                    return;
                }

                int i;
                try {
                    i = Integer.parseInt(args.get(1));
                } catch (NumberFormatException throwaway)
                {
                    m.sendError("Invalid Number");
                    return;
                }

                Document get = col.find(new Document("obj", "main")).first();
                if (get == null)
                {
                    m.sendError("An internal error has occurred.");
                    return;
                }
                if (i > Integer.parseInt((String) get.get("amount")))
                {
                    m.sendError("That error id doesn't exist!");
                    return;
                }
                Document get2 = col.find(new Document("id", Integer.toString(i))).first();
                if (get2 == null)
                {
                    m.sendError("An internal error has occurred.");
                    return;
                }
                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Removed Bug Report : " + i);
                b.withDesc("Reminder of the now deleted bug report.");
                b.withFooterText(Util.getTimeStamp());
                b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
                b.appendField("Details", get2.getString("details"), false);
                b.appendField("Author", get2.getString("author"), false);
                if (!get2.getString("file").equalsIgnoreCase("none"))
                    b.withImage(get2.getString("file"));
                m.sendEmbed(b.build());
                return;
            }

            if (args.size() == 2 && args.get(0).equalsIgnoreCase("remove"))
            {
                int i;
                try {
                    i = Integer.parseInt(args.get(1));
                } catch (NumberFormatException throwaway)
                {
                    m.sendError("Invalid Number");
                    return;
                }

                Document get = col.find(new Document("obj", "main")).first();
                if (get == null)
                {
                    m.sendError("An internal error has occurred.");
                    return;
                }
                if (i > Integer.parseInt((String) get.get("amount")))
                {
                    m.sendError("That error id doesn't exist!");
                    return;
                }
                Document get2 = col.find(new Document("id", Integer.toString(i))).first();
                if (get2 == null)
                {
                    m.sendError("An internal error has occurred.");
                    return;
                }
                EmbedBuilder b = new EmbedBuilder();
                b.withTitle("Bug Report : " + i);
                b.withFooterText(Util.getTimeStamp());
                b.withColor(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
                b.appendField("Details", get2.getString("details"), false);
                b.appendField("Author", get2.getString("author"), false);
                if (!get2.getString("file").equalsIgnoreCase("none"))
                    b.withImage(get2.getString("file"));
                m.sendEmbed(b.build());

                col.deleteOne(get2);

                int f = Integer.parseInt(get.getString("amount"))-1;
                col.updateOne(get, new Document("$set", new Document("amount", Integer.toString(f))));
                for (int i2 = 1; f > i2; i2++)
                {
                    System.out.println(i2 + " i > " + i);
                    if (i2 > i)
                    {
                        System.out.println(i2);
                        Document get3 = col.find(new Document("id", Integer.toString(i2))).first();
                        if (get3 == null)
                        {
                            m.sendError("There was an error with updating Bug Report ID's");
                            return;
                        }
                        else {
                            col.updateOne(get3, new Document("$set", new Document("id", Integer.toString(i2-1))));
                        }
                    }
                }
                return;
            }

            if (e.getMessage().getAttachments().isEmpty())
            {
                Document get = col.find(new Document("obj", "main")).first();
                Document doc = new Document();
                StringBuilder b = new StringBuilder();
                for (String s : args)
                {
                    b.append(s + " ");
                }
                doc.append("id", Integer.toString(Integer.parseInt(get.getString("amount")) + 1));
                doc.append("details", b.toString().trim());
                doc.append("author", e.getAuthor().getName());
                doc.append("file", "none");
                col.insertOne(doc);
                col.updateOne(get, new Document("$set", new Document("amount", Integer.toString(Integer.parseInt(get.getString("amount")) + 1))));
                m.send("Sent a bug report!", "Bug Reporter");
                return;
            }

            if (!e.getMessage().getAttachments().isEmpty())
            {
                Document get = col.find(new Document("obj", "main")).first();
                Document doc = new Document();
                StringBuilder b = new StringBuilder();
                for (String s : args)
                {
                    b.append(s + " ");
                }
                doc.append("id", Integer.toString(Integer.parseInt(get.getString("amount")) + 1));
                doc.append("details", b.toString().trim());
                doc.append("author", e.getAuthor().getName());
                doc.append("file", e.getMessage().getAttachments().get(0).getUrl());
                col.insertOne(doc);
                col.updateOne(get, new Document("$set", new Document("amount", Integer.toString(Integer.parseInt(get.getString("amount")) + 1))));
                m.send("Sent a bug report!", "Bug Reporter");
                return;
            }

            help(e, args);
        };*/
    }

    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args) {
        HashMap<String, String> st = new HashMap<>();
        st.put("bug <detatils>", "Reports a bug with details.");
        st.put("bug <details> <image>", "Reports a bug with details and an image.");
        return HelpHandler.helpCommand(st, "Bug Report", e);
    }
}
