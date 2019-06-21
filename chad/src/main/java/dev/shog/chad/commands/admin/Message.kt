package dev.shog.chad.commands.admin

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.coroutine.asIChannelList
import dev.shog.chad.framework.handle.coroutine.request
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import dev.shog.chad.framework.obj.Guild.DataType
import dev.shog.chad.framework.util.getChannelName
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IChannel
import java.util.*
import java.util.regex.Pattern

/**
 * Log things such as user joins or user leaves.
 *
 * @author sho
 */
class Message : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>)  {
        val messageHandler = MessageHandler(e.channel, e.author)
        val guild = GuildHandler.getGuild(e.guild.longID)

        val prefix = guild.getObject(DataType.PREFIX) as String

        // Makes sure there's arguments
        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}im help")
            return
        }

        when (args[0].toLowerCase()) {
            "join" -> setMessage("join", messageHandler, args, guild, DataType.JOIN_MESSAGE, e)
            "leave" -> setMessage("leave", messageHandler, args, guild, DataType.LEAVE_MESSAGE, e)
            "ban" -> setMessage("ban", messageHandler, args, guild, DataType.BAN_MESSAGE, e)
            "kick" -> setMessage("kick", messageHandler, args, guild, DataType.KICK_MESSAGE, e)

            "set" -> {
                if (args.size != 3) {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}im set [type] [true/false]")
                    return
                }

                val set: Boolean = when {
                    args[2].equals("true", ignoreCase = true) -> true
                    args[2].equals("false", ignoreCase = true) -> false

                    else -> {
                        messageHandler.sendError("Please use `true` or `false`!")
                        return
                    }
                }

                when (args[1].toLowerCase()) {
                    "join" -> setToggle("join", messageHandler, set, guild, DataType.JOIN_MESSAGE_ON, e)
                    "ban" -> setToggle("ban", messageHandler, set, guild, DataType.BAN_MESSAGE_ON, e)
                    "kick" -> setToggle("kick", messageHandler, set, guild, DataType.KICK_MESSAGE_ON, e)
                    "leave" -> setToggle("leave", messageHandler, set, guild, DataType.LEAVE_MESSAGE_ON, e)

                    else -> {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "im help")
                        return
                    }
                }

                return
            }

            "setchannel" -> {
                if (args.size < 3) {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}im setchannel [type] [channel name]")
                    return
                }

                val option = args[1]

                args.removeAt(0)
                args.removeAt(0)

                val stringBuilder = StringBuilder()
                for (arg in args) stringBuilder.append("$arg ")

                val channels = request {
                    e.guild.getChannelsByName(stringBuilder.toString().trim(' '))
                }.asIChannelList()

                if (channels.isEmpty()) {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}im setchannel [type] [channel name]")
                    return
                }

                when (option.toLowerCase()) {
                    "join" -> setChannel("join", messageHandler, channels[0], guild, DataType.JOIN_MESSAGE_CHANNEL, e)
                    "leave" -> setChannel("leave", messageHandler, channels[0], guild, DataType.LEAVE_MESSAGE_CHANNEL, e)

                    else -> {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "im help")
                        return
                    }
                }
                return
            }

            else -> {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "im help")
            }
        }
    }

    private fun setMessage(obj: String, messageHandler: MessageHandler, args: MutableList<String>, guild: Guild, dataType: DataType, e: MessageEvent) {
        if (args.size < 2) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${guild.getObject(DataType.PREFIX)}im kick [message]")
            return
        }

        args.removeAt(0)

        val stringBuilder = StringBuilder()
        for (arg in args) stringBuilder.append("$arg ")

        val charArray = obj.toCharArray()
        charArray[0] = charArray[0].toUpperCase()

        messageHandler.sendMessage("The guild's `$obj` message has been set to `${SMALL_CODE_BLOCK.matcher(LARGE_CODE_BLOCK.matcher(stringBuilder.toString().trim { it <= ' ' }).replaceAll("<lcb>")).replaceAll("<scb>")}`.")


        MessageHandler.sendConfigLog("${String(charArray)} Message", stringBuilder.toString().trim { it <= ' ' }, guild.getObject(dataType) as String, e.author, e.guild)
        guild.setObject(dataType, stringBuilder.toString().trim(' '))
    }

    private fun setToggle(obj: String, messageHandler: MessageHandler, set: Boolean, guild: Guild, dataType: DataType, e: MessageEvent) {
        val charArray = obj.toCharArray()
        charArray[0] = charArray[0].toUpperCase()

        messageHandler.sendMessage("`$obj` has been set to `$set`.")

        MessageHandler.sendConfigLog(String(charArray), set.toString(), guild.getObject(dataType).toString(), e.author, e.guild)
        guild.setObject(dataType, set)
    }

    private fun setChannel(obj: String, messageHandler: MessageHandler, channel: IChannel, guild: Guild, dataType: DataType, e: MessageEvent) {
        val charArray = obj.toCharArray()
        charArray[0] = charArray[0].toUpperCase()

        messageHandler.sendMessage("`$obj` channel has been set to `${channel.name}`.")

        MessageHandler.sendConfigLog("${String(charArray)} Channel", channel.name, getChannelName(guild.getObject(dataType), e.guild), e.author, e.guild)
        guild.setObject(dataType, channel.longID)
    }


    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["im join [message]"] = "Sets the join message."
        st["im leave [message]"] = "Sets the leave message."
        st["im ban [message]"] = "Sets the ban message."
        st["im kick [message]"] = "Sets the kick message."
        st["im toggle [join/leave/ban/kick] <true/false]"] = "Toggles the different message types."
        st["im setchannel [join/leave] [channel name]"] = "Toggles the join/leave messages."
        st["!TEXT!Variables"] = "The guild: `&guild&`\nThe user's name: `&user&`\nThe reason for a punishment: `&reason&` (ban & kick only)" +
                "\nThe amount of players after the action: `&count&`\nThe amount of players after the action with an ending like `st`, `rd`: `&formatted_count&`"
        Command.helpCommand(st, "Interactive Message", e)
    }

    companion object {
        private val LARGE_CODE_BLOCK = Pattern.compile("```")
        private val SMALL_CODE_BLOCK = Pattern.compile("`")
    }
}
