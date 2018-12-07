package org.woahoverflow.chad.framework.handle;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.stream.Collectors;
import org.apache.http.util.TextUtils;
import org.bson.Document;
import org.woahoverflow.chad.framework.Chad;
import org.woahoverflow.chad.framework.Util;
import org.woahoverflow.chad.framework.ui.ChadError;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Handles almost all messages within Chad
 *
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class MessageHandler
{
    // All the messages that're used in commands.
    public static final String INVALID_USER = "That user couldn't be found!";
    public static final String BOT_NO_PERMISSION = "Chad doesn't have permission for this!";
    public static final String USER_NO_PERMISSION = "You don't have permission for this command!";
    public static final String CHANNEL_NOT_NSFW = "This channel isn't NSFW!";
    public static final String INVALID_ARGUMENTS = "Invalid arguments!";
    public static final String NO_MENTIONS = "You didn't mention anyone!";
    public static final String INTERNAL_EXCEPTION = "Internal exception!";

    private final IChannel channel;

    /**
     * Public Constructor
     * @param channel The channel to send the messages in
     */
    public MessageHandler(IChannel channel)
    {
        this.channel = channel;
    }

    /**
     * Sends a raw message
     *
     * @param message The message to be sent
     */
    public final void sendMessage(String message)
    {
        // Makes sure the bot has permission in the guild
        if (!channel.getClient().getOurUser().getPermissionsForGuild(channel.getGuild()).contains(Permissions.SEND_MESSAGES))
            return;

        // Requests the message to be sent
        RequestBuffer.request(() -> channel.sendMessage(message));
    }

    /**
     * Sends an embed and applies the default values
     *
     * @param embedBuilder The embed builder to send
     */
    public final void sendEmbed(EmbedBuilder embedBuilder)
    {
        // Makes sure the bot has permission in the guild
        if (!channel.getClient().getOurUser().getPermissionsForGuild(channel.getGuild()).contains(Permissions.EMBED_LINKS))
            return;

        // Applies the timestamp to the footer
        embedBuilder.withFooterText(Util.getTimeStamp());

        // Makes the color random
        embedBuilder.withColor(new Color(new SecureRandom().nextFloat(), new SecureRandom().nextFloat(), new SecureRandom().nextFloat()));

        // Requests the message to be sent
        RequestBuffer.request(() -> channel.sendMessage(embedBuilder.build()));
    }

    /**
     * Sends an error with the default embed builder
     *
     * @param error The error string to be sent
     */
    public final void sendError(String error)
    {
        // Creates an embed builder and applies the throwError to the description
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.withTitle("Error");
        embedBuilder.withDesc(error);

        // Sends
        sendEmbed(embedBuilder);
    }

    /**
     * Builds an embed
     *
     * @param msg The message string of the embed
     * @param title The title string of the embed
     */
    public final void send(String msg, String title)
    {
        // Creates an embed builder and applies msg and title
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.withTitle(title);
        embedBuilder.withDesc(msg);

        // Sends
        sendEmbed(embedBuilder);
    }

    /**
     * Sends an embed to the guild's logging channel
     *
     * @param embedBuilder The embed to send
     * @param guild The guild to send in
     */
    public static void sendLog(EmbedBuilder embedBuilder, IGuild guild)
    {
        // Gets the guild's cached doc
        Document document = Chad.getGuild(guild).getDocument();

        // Checks if logging is enabled
        if (!document.getBoolean("logging"))
            return;

        // Gets the logging channel ID
        String channelID = document.getString("logging_channel");

        // Checks if the logging channel is somehow null
        if (TextUtils.isEmpty(channelID))
            return;

        // Checks if the id is empty, by default it's set to 'none'
        if (channelID.equalsIgnoreCase("none"))
            return;

        // Attempts to get the logging channel, catching if the string isn't actually an ID.
        IChannel loggingChannel;
        try {
            loggingChannel = RequestBuffer.request(() -> guild.getChannelByID(Long.parseLong(channelID))).get();
        } catch (NumberFormatException e)
        {
            ChadError.throwError("Guild " + guild.getStringID() + "'s logging had an issue!", e);
            return;
        }

        // Checks if the channel is deleted
        if (RequestBuffer.request(loggingChannel::isDeleted).get())
            return;

        // Applies the timestamp to the footer & applies color
        embedBuilder.withFooterText(Util.getTimeStamp()).withColor(new Color(new SecureRandom().nextFloat(), new SecureRandom().nextFloat(), new SecureRandom().nextFloat()));

        // Sends the log on it's way :)
        RequestBuffer.request(() -> loggingChannel.sendMessage(embedBuilder.build()));
    }

    /**
     * Sends a local guild log for a punishment
     *
     * @param punishment The punishment string
     * @param punished The user who's been punished
     * @param moderator The user who performed the action
     * @param guild The guild that it was performed in
     * @param reason The reason for the punishment
     */
    public static void sendPunishLog(String punishment, IUser punished, IUser moderator, IGuild guild, List<String> reason)
    {
        // Creates a string of the reasons
        String sb = reason.stream().map(s -> s + ' ').collect(Collectors.joining());

        // Creates an embed builder with all the details
        EmbedBuilder embedBuilder = new EmbedBuilder().withTitle("Punishment : " + punished.getName()).appendField("Punished User", punished.getName(), true).appendField("Moderator", moderator.getName(), true).appendField("Punishment", punishment, true).appendField("Reason", sb
            .trim(), false).withImage(punished.getAvatarURL()).withFooterText(Util.getTimeStamp());

        // Sends the log
        sendLog(embedBuilder, guild);
    }

    /**
     * Sends a local guild log for a config change
     *
     * @param changedValue The changed value
     * @param newValue The new value
     * @param oldValue The old value
     * @param moderator The user who changed the value
     * @param guild The guild in which it was changed
     */
    public static void sendConfigLog(String changedValue, String newValue, String oldValue, IUser moderator, IGuild guild)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder().withTitle("Config Change : " + changedValue).appendField("New Value", newValue, true).appendField("Old Value", oldValue, true).appendField("Admin", moderator.getName(), true).withFooterText(Util.getTimeStamp());
        sendLog(embedBuilder, guild);
    }

    /**
     * Sends a file in the channel
     *
     * @param file The file to be sent
     */
    public final void sendFile(File file)
    {
        // Makes sure the bot has permission in the guild
        if (!channel.getClient().getOurUser().getPermissionsForGuild(channel.getGuild()).contains(Permissions.ATTACH_FILES))
            return;

        RequestBuffer.request(() -> {
            try {
                channel.sendFile(file);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        });
    }
}
