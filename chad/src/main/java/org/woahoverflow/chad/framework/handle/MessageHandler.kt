package org.woahoverflow.chad.framework.handle

import org.apache.http.util.TextUtils
import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.framework.obj.Guild.DataType
import org.woahoverflow.chad.framework.util.Util
import sx.blah.discord.handle.obj.*
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.awt.Color
import java.io.File
import java.io.FileNotFoundException
import java.security.SecureRandom

/**
 * Handles almost all messages within Chad
 *
 * @author sho, codebasepw
 */
class MessageHandler(private val channel: IChannel, user: IUser) {
    /**
     * The user's avatar URL
     */
    private val avatarUrl: String = RequestBuffer.request<String> { user.avatarURL }.get()

    /**
     * The user's name
     */
    private val userName: String = RequestBuffer.request<String> { user.name }.get()

    /**
     * If there's credit within the footer text
     */
    private var credit: String? = null

    enum class Messages(
            /**
             * The message
             */
            val message: String,
            /**
             * The amount of replaceable areas in the message
             */
            val replaceable: Int)
    {
        USER_NOT_DEVELOPER("You don't have permission for this!", 0),
        INVALID_USER("That user couldn't be found!", 0), BOT_NO_PERMISSION("The bot doesn't have permission to perform this!", 0),
        USER_NO_PERMISSION("You don't have permission to perform this!", 0), CHANNEL_NOT_NSFW("This channel isn't NSFW!", 0),
        INVALID_ARGUMENTS("Invalid Usage!\nProper Usage: `<>`", 1),
        NO_MENTIONS("You didn't mention anyone!\nProper Usage: `<>`", 1),
        INTERNAL_EXCEPTION("Internal exception!", 0), INVALID_ID("Invalid ID!\n\nYou inputted: `<>`\nActual ID: `490728748501434369`", 1)
    }

    /**
     * Sends a preset error with build-in parameters
     *
     * @param message The preset error
     * @param strings The arguments that message requires
     */
    fun sendPresetError(message: Messages, vararg strings: String, includePrefix: Boolean = false) {
        if (strings.size != message.replaceable) sendPresetError(Messages.INTERNAL_EXCEPTION)

        if (includePrefix) {
            val prefix = GuildHandler.getGuild(channel.guild.longID).getObject(DataType.PREFIX) as String

            sendError(Util.buildString(prefix + message.message, *strings))
        } else sendError(Util.buildString(message.message, *strings))
    }

    /**
     * Sets credit for messages
     *
     * @param credit The credit to be given (ex: website)
     * @return This
     */
    fun credit(credit: String): MessageHandler {
        this.credit = credit

        return this
    }

    /**
     * Sends a raw message
     *
     * @param message The message to be sent
     */
    fun sendMessage(message: String): IMessage? {
        // Makes sure the bot has permission in the guild
        if (!channel.client.ourUser.getPermissionsForGuild(channel.guild).contains(Permissions.SEND_MESSAGES)) return null

        // Requests the message to be sent
        return RequestBuffer.request<IMessage> { channel.sendMessage(message) }.get()
    }

    /**
     * Sends an embed and applies the default values
     *
     * @param embedBuilder The embed builder to send
     */
    fun sendEmbed(embedBuilder: EmbedBuilder): IMessage? {
        // Makes sure the bot has permission in the guild
        if (!channel.client.ourUser.getPermissionsForGuild(channel.guild).contains(Permissions.EMBED_LINKS)) return null

        // Applies the timestamp to the footer
        embedBuilder.withTimestamp(System.currentTimeMillis())

        // Makes the color random
        embedBuilder.withColor(Color(SecureRandom().nextFloat(), SecureRandom().nextFloat(), SecureRandom().nextFloat()))

        // Adds the user who requested mark, or add credit
        val footer = if (credit == null) "Requested by $userName" else "Requested by $userName | $credit"

        embedBuilder.withFooterText(footer)
        embedBuilder.withFooterIcon(avatarUrl)

        // Requests the message to be sent
        return  RequestBuffer.request<IMessage> { channel.sendMessage(embedBuilder.build()) }.get()
    }

    /**
     * Sends an error with the default embed builder
     *
     * @param error The error string to be sent
     */
    fun sendError(error: String) {
        // Creates an embed builder and applies the error to the description
        val embedBuilder = EmbedBuilder()
        embedBuilder.withDesc(error)

        // Sends
        sendEmbed(embedBuilder)
    }

    /**
     * Sends a file in the channel
     *
     * @param file The file to be sent
     */
    fun sendFile(file: File): IMessage? {
        // Makes sure the bot has permission in the guild
        if (!channel.client.ourUser.getPermissionsForGuild(channel.guild).contains(Permissions.ATTACH_FILES)) return null

        return RequestBuffer.request<IMessage> {
            try {
                channel.sendFile(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                null
            }
        }.get()
    }

    /**
     * Sends the initialized embed builder.
     */
    fun sendEmbed(init: EmbedBuilder.() -> Unit) {
        sendEmbed(EmbedBuilder().apply(init))
    }

    companion object {
        /**
         * Sends an embed to the guild's logging channel
         *
         * @param embedBuilder The embed to send
         * @param guild The guild to send in
         */
        @JvmStatic
        fun sendLog(embedBuilder: EmbedBuilder, guild: IGuild): IMessage? {
            // Checks if logging is enabled
            if (!(GuildHandler.getGuild(guild.longID).getObject(DataType.LOGGING) as Boolean)) return null

            // Gets the logging channel ID
            val channelID = GuildHandler.getGuild(guild.longID).getObject(DataType.LOGGING_CHANNEL) as String

            // Checks if the logging channel is somehow null
            if (TextUtils.isEmpty(channelID)) return null

            // Checks if the id is empty, by default it's set to 'none'
            if (channelID.equals("none", ignoreCase = true)) return null

            // Attempts to get the logging channel, catching if the string isn't actually an ID.
            val loggingChannel: IChannel
            try {
                loggingChannel = RequestBuffer.request<IChannel> { guild.getChannelByID(java.lang.Long.parseLong(channelID)) }.get()
            } catch (e: NumberFormatException) {
                ChadInstance.getLogger().error("Guild " + guild.stringID + "'s logging had an issue!", e)
                return null
            }

            // Checks if the channel is deleted
            if (RequestBuffer.request<Boolean> { loggingChannel.isDeleted }.get()) return null

            // Applies the timestamp to the footer & applies color
            embedBuilder.withFooterText(Util.timeStamp).withColor(Color(SecureRandom().nextFloat(), SecureRandom().nextFloat(), SecureRandom().nextFloat()))

            // Sends the log on it's way :)
            return RequestBuffer.request<IMessage> { loggingChannel.sendMessage(embedBuilder.build()) }.get()
        }

        /**
         * Sends a local guild log for a punishment
         *
         * @param punishment The punishment string
         * @param punished The user who's been punished
         * @param moderator The user who performed the action
         * @param guild The guild that it was performed in
         * @param reasons The reason for the punishment
         */
        @JvmStatic
        fun sendPunishLog(punishment: String, punished: IUser, moderator: IUser, guild: IGuild, reasons: List<String>): IMessage? {
            // Creates a string of the reasons
            val stringBuilder = StringBuilder()
            for (reason in reasons) stringBuilder.append("$reason ")

            // Creates an embed builder with all the details
            val embedBuilder = EmbedBuilder().withTitle("Punishment : " + punished.name).appendField("Punished User", punished.name, true).appendField("Moderator", moderator.name, true).appendField("Punishment", punishment, true).appendField("Reason", stringBuilder.toString()
                    .trim { it <= ' ' }, false).withImage(punished.avatarURL).withFooterText(Util.timeStamp)

            // Sends the log
            return sendLog(embedBuilder, guild)
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
        @JvmStatic
        fun sendConfigLog(changedValue: String, newValue: String, oldValue: String, moderator: IUser, guild: IGuild): IMessage? {
            val embedBuilder = EmbedBuilder().withTitle("Config Change : $changedValue").appendField("New Value", newValue, true).appendField("Old Value", oldValue, true).appendField("Admin", moderator.name, true).withFooterText(Util.timeStamp)

            return sendLog(embedBuilder, guild)
        }
    }
}
