package org.woahoverflow.chad.commands.function;

import java.util.regex.Pattern;
import org.woahoverflow.chad.framework.handle.GuildHandler;
import org.woahoverflow.chad.framework.obj.Command;
import org.woahoverflow.chad.framework.obj.Command.Class;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import org.woahoverflow.chad.framework.handle.PermissionHandler;
import org.woahoverflow.chad.framework.obj.Guild;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class Permissions implements Class  {

    private static final Pattern COMMA = Pattern.compile(",");

    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel(), e.getAuthor());

            // Accesses the permissions to a specific role
            if (args.size() >= 3 && args.get(0).equalsIgnoreCase("role"))
            {
                // Removes the base argument, in this case "role", so it can get the role name
                args.remove(0);

                // Assign variables
                StringBuilder stringBuilder = new StringBuilder();
                IRole role = null;
                int i = 0;

                // Builds the role name
                for (String s : args)
                {
                    // Adds the amount of arguments used in the role name so they can be removed later
                    i++;

                    // Appends the string
                    stringBuilder.append(s).append(' ');

                    // Requests the roles from the guild
                    List<IRole> rolesList = RequestBuffer.request(() -> e.getGuild().getRoles())
                        .get();

                    // Checks if any of the roles equal
                    for (IRole rol : rolesList)
                        if (rol.getName().equalsIgnoreCase(stringBuilder.toString().trim()))
                            role = rol; // If a role was found, assign it to the variable

                    // If the role was assigned, break out of the loop
                    if (role != null) break;
                }

                // Make sure there's enough arguments for the rest
                if (args.size() == i)
                {
                    messageHandler.sendError("Invalid Role!");
                    return;
                }

                // Makes sure role isn't null
                if (role == null)
                {
                    messageHandler.sendError("Invalid Role!");
                    return;
                }

                // Removes the amount of arguments that the role name used
                int i1 = 0;
                while (i > i1)
                {
                    i1++;
                    args.remove(0);
                }

                // Gets the option
                String option = args.get(0);

                // Isolates the next option(s)
                args.remove(0);

                // The guild's instance
                Guild guild = GuildHandler.handle.getGuild(e.getGuild().getLongID());

                switch (option.toLowerCase()) {
                    case "add":
                        // The add can only add 1 command
                        if (args.size() != 1)
                        {
                            messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                            return;
                        }

                        // Adds it to the database and gets the result
                        int add = guild.addPermissionToRole(role.getLongID(), args.get(0).toLowerCase());

                        // If the result was 0 (good) return the amount, if not return the correct error.
                        if (add == 0)
                            messageHandler.sendEmbed(
                                new EmbedBuilder()
                                    .withDesc("Added `" + args.get(0).toLowerCase() + "` command to role `" + role.getName() + "`.")
                                    .withTitle("Permissions"));
                        else
                            messageHandler.sendError(PermissionHandler.handle.parseErrorCode(add));
                        return;
                    case "remove":
                        // The remove can only remove 1 command
                        if (args.size() != 1)
                        {
                            messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                            return;
                        }

                        // Removes it from the database and gets the result
                        int rem = guild.removePermissionFromRole(role.getLongID(), args.get(0).toLowerCase());

                        // If the result was 0 (good) return the amount, if not return the correct error.
                        if (rem == 0)
                            messageHandler.sendEmbed(new EmbedBuilder()
                                .withDesc("Removed `" + args.get(0).toLowerCase() + "` command from role `" + role.getName() + "`.")
                                .withTitle("Permissions"));
                        else
                            messageHandler.sendError(PermissionHandler.handle.parseErrorCode(rem));
                        return;
                    case "view":
                        // Gets the permissions to a role
                        ArrayList<String> ar = guild.getRolePermissions(role.getLongID());

                        // Checks if there's no permissions
                        if (ar == null || ar.isEmpty()) {
                            messageHandler.sendError("There's no permissions in this role!");
                            return;
                        }

                        // Creates an embed builder and applies the title
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.withTitle("Viewing Permissions for `" + role.getName()+ '`');

                        // Builds all the permissions
                        StringBuilder stringBuilder2 = new StringBuilder();
                        ar.forEach((v) -> stringBuilder2.append(", ").append(v));

                        // Replaces the first ',' and sends.
                        embedBuilder.withDesc(
                            COMMA.matcher(stringBuilder2.toString().trim()).replaceFirst(""));
                        messageHandler.sendEmbed(embedBuilder);
                        return;
                    default:
                        messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                        return;
                }
            }
            messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        HashMap<String, String> st = new HashMap<>();
        st.put("perm role <role name> add <command>", "Adds a Chad command to a Discord role.");
        st.put("perm role <role name> remove <command>", "Removes a Chad command to a Discord role.");
        st.put("perm role <role name> view", "Displays all Chad commands tied to that Discord role.");
        return Command.helpCommand(st, "Permissions", e);
    }
}

