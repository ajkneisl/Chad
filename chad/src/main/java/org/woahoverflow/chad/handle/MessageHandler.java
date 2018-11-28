package org.woahoverflow.chad.handle;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.stream.Collectors;
import org.apache.http.util.TextUtils;
import org.bson.Document;
import org.woahoverflow.chad.handle.ui.ChadError;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class MessageHandler
{
    // All the messages that're used in commands.
    public static final String INVALID_USER = "That user couldn't be found!";
    public static final String BOT_NO_PERMISSION = "Chad doesn't have permission for this!";
    public static final String USER_NO_PERMISSION = "You don't have permission for this command!";
    public static final String CHANNEL_NOT_NSFW = "This channel isn't Nsfw!";
    public static final String INVALID_ARGUMENTS = "Invalid Arguments!";
    public static final String NO_MENTIONS = "You didn't mention anyone!";
    public static final String INTERNAL_EXCEPTION = "Internal Exception!";

    private final IChannel channel;
    public MessageHandler(IChannel channel)
    {
        this.channel = channel;
    }

    // Sends a raw message
    public final void sendMessage(String message)
    {
        // Requests the message to be sent
        RequestBuffer.request(() ->channel.sendMessage(message));
    }

    // Sends the embed and applies default items.
    public final void sendEmbed(EmbedBuilder embedBuilder)
    {
        // Applies the timestamp to the footer
        embedBuilder.withFooterText(Util.getTimeStamp());

        // Makes the color random
        embedBuilder.withColor(new Color(new SecureRandom().nextFloat(), new SecureRandom().nextFloat(), new SecureRandom().nextFloat()));

        // Requests the message to be sent
        RequestBuffer.request(() -> channel.sendMessage(embedBuilder.build()));
    }

    // Default method for sending errors.
    public final void sendError(String error)
    {
        // Creates an embed builder and applies the throwError to the description
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.withTitle("Error");
        embedBuilder.withDesc(error);

        // Sends
        sendEmbed(embedBuilder);
    }

    // Sends a embed message with a title and message.
    public final void send(String msg, String title)
    {
        // Creates an embed builder and applies msg and title
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.withTitle(title);
        embedBuilder.withDesc(msg);

        // Sends
        sendEmbed(embedBuilder);
    }

    // Base log sender
    public static void sendLog(EmbedBuilder embedBuilder, IGuild guild)
    {
        // Gets the guild's cached doc
        Document document = CachingHandler.getGuild(guild).getDoc();

        // Checks if logging is enabled
        if (!document.getBoolean("logging"))
        {
            return;
        }

        // Gets the logging channel ID
        String channelID = document.getString("logging_channel");

        // Checks if the logging channel is somehow null
        if (TextUtils.isEmpty(channelID))
        {
            return;
        }

        // Checks if the id is empty, by default it's set to 'none'
        if (channelID.equalsIgnoreCase("none"))
        {
            return;
        }

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
        {
            return;
        }

        // Applies the timestamp to the footer & applies color
        embedBuilder.withFooterText(Util.getTimeStamp()).withColor(new Color(new SecureRandom().nextFloat(), new SecureRandom().nextFloat(), new SecureRandom().nextFloat()));

        // Sends the log on it's way :)
        RequestBuffer.request(() -> loggingChannel.sendMessage(embedBuilder.build()));
    }

    // Logger for punishments
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

    // Logger for config changes within the guild
    public static void sendConfigLog(String changedValue, String newValue, String oldValue, IUser moderator, IGuild guild)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder().withTitle("Config Change : " + changedValue).appendField("New Value", newValue, true).appendField("Old Value", oldValue, true).appendField("Admin", moderator.getName(), true).withFooterText(Util.getTimeStamp());
        sendLog(embedBuilder, guild);
    }

    // Sends a file
    public final void sendFile(File file)
    {
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
