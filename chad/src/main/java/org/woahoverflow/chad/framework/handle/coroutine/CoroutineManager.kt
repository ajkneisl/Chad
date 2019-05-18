package org.woahoverflow.chad.framework.handle.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PermissionHandler
import org.woahoverflow.chad.framework.handle.xp.XPHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.RequestBuffer
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

class CoroutineManager internal constructor(): CoroutineScope by CoroutineScope(Dispatchers.Unconfined) {
    /**
     * The user's and how many threads they're running.
     */
    val users = ConcurrentHashMap<IUser, Int>()

    /**
     * Dispatches the events.
     */
    @EventSubscriber
    fun messageReceived(event: MessageReceivedEvent) {
        if (event.guild == null) return

        if (event.author.isBot) return

        val argArray = event.message.content.split(" ")
        if (argArray.isEmpty()) return

        XPHandler.getUserInstance(event.author).registerChat(event.message)

        val guild = GuildHandler.getGuild(event.guild.longID)
        guild.messageSent()

        if (guild.getObject(Guild.DataType.SWEAR_FILTER) as Boolean) {
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

        val prefix: String = guild.getObject(Guild.DataType.PREFIX) as String
        if (!argArray[0].toLowerCase().startsWith(prefix)) return

        val commandString = argArray[0].substring(prefix.length).toLowerCase()

        val args = ArrayList(Arrays.asList<String>(*argArray.toTypedArray()))
        args.removeAt(0)

        if (!event.author.canRun()) return

        var command: Command.Data? = null
        var commandName: String? = null

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

        if (command == null) return

        if (command.commandCategory == Command.Category.DEVELOPER && !PermissionHandler.isDeveloper(event.author)) {
            MessageHandler(event.channel, event.author).sendPresetError(MessageHandler.Messages.USER_NOT_DEVELOPER)
            return
        }

        if ((guild.getObject(Guild.DataType.DISABLED_CATEGORIES) as ArrayList<*>).contains(command.commandCategory.toString().toLowerCase())) return

        if (!PermissionHandler.hasPermission(commandName!!, event.author, event.guild) && !event.author.getPermissionsForGuild(event.guild).contains(Permissions.ADMINISTRATOR)) {
            MessageHandler(event.channel, event.author).sendPresetError(MessageHandler.Messages.USER_NO_PERMISSION)
            return
        }

        launch {
            ChadInstance.getLogger().debug("Starting task for user ${event.author.stringID}: $commandName")
            synchronized(users) {
                users[event.author] = if (users[event.author] != null) users[event.author]!! else 0 + 1
            }

            val successful: Boolean = try {
                if (args.size == 1 && args[0].equals("help", ignoreCase = true))
                    command.commandClass.help(event)
                else command.commandClass.run(event, args)

                true
            } catch (ex: Exception) {
                request { event.channel.sendMessage("There was an issue running that command!\nError: `${ex.message}`") }
                false
            }

            synchronized(users) {
                users[event.author] = users[event.author]!! - 1
            }

            if (successful) {
                ChadInstance.getLogger().debug("Finished task successfully for user ${event.author.name}")
            } else {
                ChadInstance.getLogger().debug("Finished task unsuccessfully for user ${event.author.name}")
            }
        }
    }

    /**
     * If an IUser can run a coroutine.
     */
    private fun IUser.canRun(): Boolean = !(users.containsKey(this) && users[this]!! >= 3)

    companion object {
        /**
         * The main instance of the Coroutine Manager.
         */
        @JvmStatic
        val instance = CoroutineManager()
    }
}