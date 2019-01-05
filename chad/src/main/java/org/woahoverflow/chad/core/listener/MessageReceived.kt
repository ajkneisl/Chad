package org.woahoverflow.chad.core.listener

import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.Chad
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PermissionHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.Permissions
import java.util.*

class MessageReceived {
    @EventSubscriber
    fun messageReceived(event: MessageReceivedEvent) {
        // The message split into each word
        val argArray = event.message.content.split(" ")

        // If there's none, return
        if (argArray.isEmpty())
            return

        // The guild's instance
        val guild = GuildHandler.handle.getGuild(event.guild.longID)

        // Updates the guild's statistics
        guild.messageSent()

        // The guild's prefix, if the message starts with this expect it's a command
        val prefix: String = guild.getObject(Guild.DataType.PREFIX) as String

        // Check if message is an actual command
        if (!argArray[0].toLowerCase().startsWith(prefix))
            return

        // The command string aka the first argument without the prefix
        val commandString = argArray[0].substring(prefix.length).toLowerCase()

        // The message without the command string
        val args = ArrayList(Arrays.asList<String>(*argArray.toTypedArray()))
        args.removeAt(0)

        // The user's thread consumer
        val threadConsumer: Chad.ThreadConsumer = Chad.getConsumer(event.author.longID)

        // If the user doesn't have 3 threads currently running
        if (!Chad.consumerRunThread(threadConsumer))
            return

        // The found command's data, unless if it isn't found it's null
        var command: Command.Data? = null
        var commandName: String? = null

        // Iterates through all of the actual commands to see if it's valid
        for (cmd in ChadVar.COMMANDS.keys) {
            val data = ChadVar.COMMANDS[cmd]!!

            if (commandString.equals(cmd, true)) {
                command = data
                commandName = cmd
                break
            }

            if (data.usesAliases()) {
                for (cmdAlias in data.commandAliases) {
                    if (commandString.equals(cmdAlias, true)) {
                        command = data
                        commandName = cmd
                        break
                    }
                }
            }
        }

        if (command == null)
            return

        // if the command is developer only, and the user is NOT a developer, deny them access
        if (command.commandCategory == Command.Category.DEVELOPER && !PermissionHandler.handle.userIsDeveloper(event.author)) {
            MessageHandler(event.channel, event.author).sendError("This command is Developer only!")
            return
        }

        // if the category is disabled
        if ((guild.getObject(Guild.DataType.DISABLED_CATEGORIES) as ArrayList<*>).contains(command.commandCategory.toString().toLowerCase())) {
            return
        }

        // if the user does NOT have permission for the command, and does NOT have the administrator permission, deny them access
        if (PermissionHandler.handle.userNoPermission(commandName, event.author, event.guild) && !event.author.getPermissionsForGuild(event.guild).contains(Permissions.ADMINISTRATOR)) {
            MessageHandler(event.channel, event.author).sendPresetError(MessageHandler.Messages.USER_NO_PERMISSION)
            return
        }

        val thread = if (args.size == 1 && args[0].equals("help", ignoreCase = true)) command.commandClass.help(event) else command.commandClass.run(event, args)

        // add the command thread to the handler
        Chad.runThread(thread, threadConsumer)
    }
}