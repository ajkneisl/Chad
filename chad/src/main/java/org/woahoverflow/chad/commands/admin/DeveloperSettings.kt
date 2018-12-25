package org.woahoverflow.chad.commands.admin

import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.core.ChadVar.*
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PlayerHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.obj.Player
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.EmbedBuilder
import java.lang.StringBuilder
import java.util.HashMap

/**
 * Developer options, created in Kotlin :)
 *
 * @author sho
 * @since 0.7.1
 */
class DeveloperSettings : Command.Class {
    override fun help(e: MessageReceivedEvent): Runnable {
        val st = HashMap<String, String>()
        st["devsettings <option> [arguments]"] = "Modify different settings."
        st["devsettings view"] = "View all options"
        return Command.helpCommand(st, "Developer Settings", e)
    }

    override fun run(e: MessageReceivedEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val messageHandler = MessageHandler(e.channel, e.author)

            if (args.isEmpty()) {
                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS)
                return@Runnable
            }

            // If they're trying to view all available options
            if (args.size == 1 && args[0].toLowerCase() == "view") {
                messageHandler.sendEmbed(EmbedBuilder().withDesc("__Valid Developer Settings__" +
                        "\n\n**cache <reset> [arguments]** Adjust caching options" +
                        "\n\n**devs <add/remove/view> [id]** Adjust the people with developer role" +
                        "\n\n**adj <user/guild> <id> <data type> <new value>** Adjust a guilds/users data"))
                return@Runnable
            }

            when (args[0].toLowerCase()) {
                // If they're adjusting a guild/user's data
                "adj" -> {
                    if (args.size == 1) {
                        messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS)
                        return@Runnable
                    }

                    // If they're adjusting the values of a guild
                    if (args[1].equals("guild", true)) {
                        // Makes sure the ID is a proper long
                        try {
                            args[2].toLong()
                        } catch (e: NumberFormatException) {
                            messageHandler.sendError("Invalid ID!")
                            return@Runnable
                        }

                        // Get the ID
                        val id = args[2].toLong()

                        // Just to try to remove the possibility of inputting an invalid guild
                        if (id.toString().length != 18) {
                            messageHandler.sendError("Invalid ID!")
                            return@Runnable
                        }

                        // If the guild doesn't already exist, don't do it
                        if (!GuildHandler.handle.guildExists(id)) {
                            messageHandler.sendError("Invalid ID!")
                            return@Runnable
                        }

                        val guild = GuildHandler.handle.getGuild(id)

                        var datatype = Guild.DataType.values()[0]
                        var success = false

                        // Parse through the datatypes to see if it's valid
                        for (type in Guild.DataType.values()) {
                            if (args[3].equals(type.toString(), true)) {
                                datatype = type
                                success = true
                            }
                        }

                        // If it's invalid
                        if (!success) {
                            messageHandler.sendError("Invalid Type")
                            return@Runnable
                        }

                        val stringBuilder = StringBuilder()

                        // Removes all of the previous arguments to build the new value
                        args.removeAt(0) // adj
                        args.removeAt(0) // guild
                        args.removeAt(0) // id
                        args.removeAt(0) // datatype

                        for (st in args) {
                            stringBuilder.append("$st ")
                        }

                        val string = stringBuilder.toString().trim()

                        if (string.endsWith("!long")) {
                            try {
                                string.removeSuffix("!long").toLong()
                            } catch (e: Exception) {
                                messageHandler.sendEmbed(EmbedBuilder().withDesc("Invalid Long!"))
                                return@Runnable
                            }
                            guild.setObject(datatype, string.removeSuffix("!long").toLong())
                            messageHandler.sendEmbed(EmbedBuilder().withDesc("Changed `${datatype.toString().toLowerCase()}` to `${string.removeSuffix("!long").toLong()}`!"))
                            return@Runnable
                        }

                        if (string.endsWith("!int")) {
                            try {
                                string.removeSuffix("!int").toInt()
                            } catch (e: Exception) {
                                messageHandler.sendEmbed(EmbedBuilder().withDesc("Invalid Integer!"))
                                return@Runnable
                            }
                            guild.setObject(datatype, string.removeSuffix("!int").toInt())
                            messageHandler.sendEmbed(EmbedBuilder().withDesc("Changed `${datatype.toString().toLowerCase()}` to `${string.removeSuffix("!int").toInt()}"))
                            return@Runnable
                        }

                        // If it's true, set as true
                        if (string.equals("true", true)) {
                            guild.setObject(datatype, true)
                            messageHandler.sendEmbed(EmbedBuilder().withDesc("Changed ${datatype.toString().toLowerCase()} to `true`!"))
                        }

                        // If it's false, set as false
                        if (string.equals("false", true)) {
                            guild.setObject(datatype, false)
                            messageHandler.sendEmbed(EmbedBuilder().withDesc("Changed `${datatype.toString().toLowerCase()}` to `false`!"))
                            return@Runnable
                        }

                        guild.setObject(datatype, string)
                        messageHandler.sendEmbed(EmbedBuilder().withDesc("Changed `${datatype.toString().toLowerCase()}` to `$string`!"))
                        return@Runnable
                    }

                    // -- previously commented with the guild --
                    if (args[1].equals("user", true)) {
                        try {
                            args[2].toLong()
                        } catch (e: NumberFormatException) {
                            messageHandler.sendError("Invalid ID!")
                            return@Runnable
                        }

                        val id = args[2].toLong()

                        // Just to try to remove the possibility of inputting an invalid user
                        if (id.toString().length != 18) {
                            messageHandler.sendError("Invalid ID!")
                            return@Runnable
                        }

                        // If the user doesn't already exist, don't do it
                        if (!PlayerHandler.handle.playerExists(id)) {
                            messageHandler.sendError("Invalid ID!")
                            return@Runnable
                        }

                        val player = PlayerHandler.handle.getPlayer(id)

                        var datatype = Player.DataType.values()[0]
                        var success = false

                        for (type in Player.DataType.values()) {
                            if (args[3].equals(type.toString(), true)) {
                                datatype = type
                                success = true
                            }
                        }

                        if (!success) {
                            messageHandler.sendError("Invalid Type")
                            return@Runnable
                        }

                        val stringBuilder = StringBuilder()

                        args.removeAt(0) // adj
                        args.removeAt(0) // player
                        args.removeAt(0) // id
                        args.removeAt(0) // datatype

                        for (st in args) {
                            stringBuilder.append("$st ")
                        }

                        val string = stringBuilder.toString().trim()

                        if (string.endsWith("!long")) {
                            try {
                                string.removeSuffix("!long").toLong()
                            } catch (e: Exception) {
                                messageHandler.sendEmbed(EmbedBuilder().withDesc("Invalid Long!"))
                                return@Runnable
                            }
                            player.setObject(datatype, string.removeSuffix("!long").toLong())
                            messageHandler.sendEmbed(EmbedBuilder().withDesc("Changed `${datatype.toString().toLowerCase()}` to `${string.removeSuffix("!long").toLong()}`!"))
                            return@Runnable
                        }

                        if (string.endsWith("!int")) {
                            try {
                                string.removeSuffix("!int").toInt()
                            } catch (e: Exception) {
                                messageHandler.sendEmbed(EmbedBuilder().withDesc("Invalid Integer!"))
                                return@Runnable
                            }
                            player.setObject(datatype, string.removeSuffix("!int").toInt())
                            messageHandler.sendEmbed(EmbedBuilder().withDesc("Changed `${datatype.toString().toLowerCase()}` to `${string.removeSuffix("!int").toInt()}"))
                            return@Runnable
                        }

                        // If it's true, set as true
                        if (string.equals("true", true)) {
                            player.setObject(datatype, true)
                            messageHandler.sendEmbed(EmbedBuilder().withDesc("Changed ${datatype.toString().toLowerCase()} to `true`!"))
                            return@Runnable
                        }

                        // If it's false, set as false
                        if (string.equals("false", true)) {
                            player.setObject(datatype, false)
                            messageHandler.sendEmbed(EmbedBuilder().withDesc("Changed `${datatype.toString().toLowerCase()}` to `false`!"))
                            return@Runnable
                        }

                        player.setObject(datatype, string)
                        messageHandler.sendEmbed(EmbedBuilder().withDesc("Changed `${datatype.toString().toLowerCase()}` to `$string`!"))
                        return@Runnable
                    }
                }

                // If they're updating cache values
                "cache" -> {
                    // If there's only one argument, assume they're trying to get the possible options
                    if (args.size == 1) {
                        messageHandler.sendEmbed(EmbedBuilder().withDesc("Caching Options" +
                                "\n\n**reset <type>** Resets a cached area.\nValid: __eightball__, __swearwords__, __guilds__, __users__, __developers__"))

                        return@Runnable
                    }

                    // Resetting something
                    if (args[1].equals("reset", ignoreCase = true)) {
                        when (args[2]) {
                            "eightball" -> {
                                // Clear the existing cached swear words
                                ChadVar.eightBallResults.clear()

                                // ReAdds all of the found ones
                                JsonHandler.handle.readArray("https://cdn.woahoverflow.org/data/chad/8ball.json").forEach { word -> eightBallResults.add(word as String) }

                                // Send confirmation
                                messageHandler.sendEmbed(EmbedBuilder().withDesc("Successfully reset `eightball` cache!"))

                                return@Runnable
                            }

                            "swearwords" -> {
                                // Clear the existing cached swear words
                                ChadVar.swearWords.clear()

                                // ReAdds all of the found ones
                                JsonHandler.handle.readArray("https://cdn.woahoverflow.org/data/chad/swears.json").forEach { word -> swearWords.add(word as String) }

                                // Send confirmation
                                messageHandler.sendEmbed(EmbedBuilder().withDesc("Successfully reset `swearwords` cache!"))

                                return@Runnable
                            }

                            // With a large amount of guilds connected to Chad, this task could take a while.
                            "guilds" -> {
                                val guilds = e.client.guilds

                                messageHandler.sendEmbed(EmbedBuilder().withDesc("Re-Caching all Chad guilds..."))

                                for (guild in guilds) {
                                    GuildHandler.handle.refreshGuild(guild.longID)
                                }

                                messageHandler.sendEmbed(EmbedBuilder().withDesc("Competed re-caching all Chad guilds!"))

                                return@Runnable
                            }

                            // With a large amount of guilds connected to Chad, this task could take a while.
                            "users" -> {
                                val guilds = e.client.guilds

                                messageHandler.sendEmbed(EmbedBuilder().withDesc("Re-Caching all Chad users... (this may take a while)"))

                                guilds
                                        .asSequence()
                                        .map { it.users }
                                        .flatMap { it.asSequence() }
                                        .forEach { PlayerHandler.handle.refreshPlayer(it.longID) }

                                messageHandler.sendEmbed(EmbedBuilder().withDesc("Competed re-caching all Chad users!"))
                                return@Runnable
                            }

                            else -> {
                                messageHandler.sendEmbed(EmbedBuilder().withDesc("Invalid Type!"))

                                return@Runnable
                            }
                        }
                    }
                }

                "devs" -> {

                    if (args.size == 1) {
                        messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS)
                        return@Runnable
                    }

                    when (args[1].toLowerCase()) {
                        "view" -> {
                            val stringBuilder = StringBuilder()

                            // Build together all of the developer's ids
                            for (dev in DEVELOPERS) {
                                stringBuilder.append("`$dev`, ")
                            }

                            messageHandler.sendEmbed(EmbedBuilder().withDesc("Users with Developer role within Chad\n\n" + stringBuilder.toString().removeSuffix(", ")))
                            return@Runnable
                        }

                        "remove" -> {
                            // cfg devs remove id
                            if (args.size != 3) {
                                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS)
                                return@Runnable
                            }

                            // Make sure te suggested ID is valid (valid enough)
                            try {
                                args[2].toLong()
                            } catch (e: Exception) {
                                messageHandler.sendError("Invalid ID!")
                                return@Runnable
                            }

                            val user = args[2].toLong()

                            // The ID's are 18 characters
                            if (user.toString().length != 18) {
                                messageHandler.sendError("Invalid ID!")
                                return@Runnable
                            }

                            if (!ChadVar.DEVELOPERS.contains(user)) {
                                messageHandler.sendError("`$user` isn't a developer!")
                                return@Runnable
                            }

                            ChadVar.DEVELOPERS.remove(user)

                            messageHandler.sendEmbed(EmbedBuilder().withDesc("`$user` is temporarily no longer a developer!"))

                            return@Runnable
                        }

                        "add" -> {
                            // cfg devs add id
                            if (args.size != 3) {
                                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS)
                                return@Runnable
                            }

                            // Make sure te suggested ID is valid (valid enough)
                            try {
                                args[2].toLong()
                            } catch (e: Exception) {
                                messageHandler.sendError("Invalid ID!")
                                return@Runnable
                            }

                            val user = args[2].toLong()

                            // The ID's are 18 characters
                            if (user.toString().length != 18) {
                                messageHandler.sendError("Invalid ID!")
                                return@Runnable
                            }

                            if (ChadVar.DEVELOPERS.contains(user)) {
                                messageHandler.sendError("`$user` is already a developer!")
                                return@Runnable
                            }

                            ChadVar.DEVELOPERS.add(user)

                            messageHandler.sendEmbed(EmbedBuilder().withDesc("`$user` is now a temporary developer!"))

                            return@Runnable
                        }

                        else -> {
                            messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS)
                            return@Runnable
                        }
                    }
                }

                else -> {
                    messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS)
                    return@Runnable
                }
            }
        }
    }
}