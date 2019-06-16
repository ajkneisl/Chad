package dev.shog.chad.commands.info

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Gets info about a discord user
 *
 * @author sho, codebasepw
 */
class UserInfo : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val u: IUser
        val messageHandler = MessageHandler(e.channel, e.author)

        // Gets the user from the mentions
        if (e.message.mentions.isNotEmpty() && args.size == 1)
            u = e.message.mentions[0]
        else {
            // If user wasn't mentioned, return
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX).toString() + "userinfo [@user]")
            return
        }

        var str = ""
        for (role in u.getRolesForGuild(e.guild)) {
            if (!role.isEveryoneRole)
                str += "${role.name}, "
        }

        str = str.removeSuffix(", ")

        val embedBuilder = EmbedBuilder()

        // If the user has no roles, set to none, if not add the roles.
        val roleString = if (str.isBlank())
            "none"
        else
            "$str [${u.getRolesForGuild(e.guild).size-1}]"

        // If the user is a bot, no, if they're a human yes
        val human = if (u.isBot) "No" else "Yes"

        // To make the dates look slightly better
        val format = SimpleDateFormat("MM/dd/yyyy")

        // Sets the description
        embedBuilder.withDesc(
                "**Username** : `" + u.name + '#'.toString() + u.discriminator + "`" +
                        "\n**Human** : `" + human + '`'.toString() +
                        "\n**Roles** : `" + roleString + '`'.toString() +
                        "\n**Guild Join Date** : `" + format.format(Date.from(e.guild.getJoinTimeForUser(u))) + '`'.toString() +
                        "\n**Account Creation Date** : `" + format.format(Date.from(u.creationDate)) + '`'.toString()
        )

        // Sends the embed with the user's avatar.
        embedBuilder.withImage(u.avatarURL)
        messageHandler.sendEmbed(embedBuilder)
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["userinfo [@user]"] = "Gives information about the mentioned user."
        Command.helpCommand(st, "User Info", e)
    }
}
