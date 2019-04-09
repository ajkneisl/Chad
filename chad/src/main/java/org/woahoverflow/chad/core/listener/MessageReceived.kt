package org.woahoverflow.chad.core.listener

import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PermissionHandler
import org.woahoverflow.chad.framework.handle.ThreadHandler
import org.woahoverflow.chad.framework.handle.xp.XPHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.RequestBuffer
import java.util.*
import java.util.regex.Pattern

class MessageReceived {
    @EventSubscriber
    fun messageReceived(event: MessageReceivedEvent) {
        if (event.guild == null) return

        if (event.author.isBot) return

        // The message split into each word
        val argArray = event.message.content.split(" ")

        // If there's none, return
        if (argArray.isEmpty()) return

        // Registers that a user chatted
        XPHandler.getUserInstance(event.author).registerChat(event.message)

        // The guild's instance
        val guild = GuildHandler.getGuild(event.guild.longID)

        // Updates the guild's statistics
        guild.messageSent()

        // If swear filter is enabled
        val stopSwear = guild.getObject(Guild.DataType.SWEAR_FILTER) as Boolean

        if (stopSwear) {
            // Builds together the message & removes the special characters
            var character = argArray.joinToString("")
            val pt = Pattern.compile("[^a-zA-Z0-9]")
            val match = pt.matcher(character)

            while (match.find()) {
                character = character.replace(("\\" + match.group()).toRegex(), "")
            }

            // Checks if the word contains a swear word
            for (swearWord in ChadVar.swearWords) {
                // Ass is a special case, due to words like `bass`
                if (swearWord.equals("ass", ignoreCase = true) && character.contains("ass", ignoreCase = true)) {
                    // Goes through all of the arguments
                    for (argument in argArray) {
                        // If the argument is just ass
                        if (argument.equals("ass", ignoreCase = true)) {
                            // Delete it
                            RequestBuffer.request { event.message.delete() }
                            return
                        }
                    }
                    continue
                }

                // If it contains any other swear word, delete it
                if (character.toLowerCase().contains(swearWord)) {
                    RequestBuffer.request { event.message.delete() }
                    return
                }
            }
        }

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

        // If the user doesn't have 3 threads currently running
        if (!ThreadHandler.canUserRunThread(event.author.longID))
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
                for (cmdAlias in data.cmdAliases!!) {
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
        if (command.commandCategory == Command.Category.DEVELOPER && !PermissionHandler.userIsDeveloper(event.author)) {
            MessageHandler(event.channel, event.author).sendPresetError(MessageHandler.Messages.USER_NOT_DEVELOPER)
            return
        }

        // if the category is disabled
        if ((guild.getObject(Guild.DataType.DISABLED_CATEGORIES) as ArrayList<*>).contains(command.commandCategory.toString().toLowerCase())) {
            return
        }

        // if the user does NOT have permission for the command, and does NOT have the administrator permission, deny them access
        if (PermissionHandler.userNoPermission(commandName!!, event.author, event.guild) && !event.author.getPermissionsForGuild(event.guild).contains(Permissions.ADMINISTRATOR)) {
            MessageHandler(event.channel, event.author).sendPresetError(MessageHandler.Messages.USER_NO_PERMISSION)
            return
        }

        val thread = if (args.size == 1 && args[0].equals("help", ignoreCase = true)) command.commandClass.help(event) else command.commandClass.run(event, args)

        // add the command thread to the handler
        ThreadHandler.runThread(thread, event.author.longID)
    }
}