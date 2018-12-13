package org.woahoverflow.chad.commands.function;

import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.handle.database.DatabaseManager;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author codebasepw
 * @since 0.6.3 B2
 */
public class AutoRole implements Command.Class  {

    @Override
    public final Runnable run(MessageReceivedEvent e, List<String> args) {
        return() -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Makes sure the arguments are empty
            if (args.isEmpty())
            {
                messageHandler.sendError("Invalid Arguments");
                return;
            }

            // Gets the option
            String option = args.get(0).toLowerCase();

            switch (option) {
                case "on":
                    // Sets the value in the database
                    DatabaseManager.GUILD_DATA.setObject(e.getGuild().getLongID(), "role_on_join", true);

                    // ReCaches the guild
                    Chad.getGuild(e.getGuild().getLongID()).cache();

                    // Builds the embed and sends it
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.withTitle("Auto Role");
                    embedBuilder.withDesc("Auto Role enabled.");
                    messageHandler.sendEmbed(embedBuilder);
                    break;
                case "off":
                    // Sets the value in the database
                    DatabaseManager.GUILD_DATA.setObject(e.getGuild().getLongID(), "role_on_join", false);

                    // ReCaches the guild
                    Chad.getGuild(e.getGuild().getLongID()).cache();

                    // Builds the embed and sends it
                    EmbedBuilder embedBuilder2 = new EmbedBuilder();
                    embedBuilder2.withTitle("Auto Role");
                    embedBuilder2.withDesc("Auto Role disabled.");
                    messageHandler.sendEmbed(embedBuilder2);
                    break;
                case "set":
                    // Isolates the role text
                    args.remove(0);

                    // Set variables
                    StringBuilder stringBuilder = new StringBuilder();
                    List<IRole> roles = new ArrayList<>();

                    // Gets roles with the text said
                    for (String s : args) {
                        stringBuilder.append(s).append(' ');
                        roles = RequestBuffer.request(() -> e.getGuild().getRolesByName(stringBuilder.toString().trim())).get();
                        if (!roles.isEmpty())
                            break;
                    }

                    // If there's no roles, return
                    if (roles.isEmpty())
                    {
                        messageHandler.sendError("Invalid Role!");
                        return;
                    }

                    // The selected role
                    IRole newRole = roles.get(0);

                    // Sets the role ID into the database and recaches
                    DatabaseManager.GUILD_DATA.setObject(e.getGuild().getLongID(), "join_role", newRole.getStringID());
                    Chad.getGuild(e.getGuild().getLongID()).cache();

                    // Builds the embed and sends it
                    EmbedBuilder embedBuilder3 = new EmbedBuilder();
                    embedBuilder3.withTitle("Auto Role");
                    embedBuilder3.withDesc("New users will now automatically receive the role: " + newRole.getName());
                    messageHandler.sendEmbed(embedBuilder3);
                    break;
                default:
                    messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                    break;
            }
        };
    }

    @Override
    public final Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("autorole <on/off>", "Toggles automatic role assignment features.");
        st.put("autorole set <role name>", "Sets role.");
        return Command.helpCommand(st, "Auto Role", e);
    }
}
