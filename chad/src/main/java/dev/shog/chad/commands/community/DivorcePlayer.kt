package dev.shog.chad.commands.community

import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.PlayerHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Player.DataType
import dev.shog.chad.framework.util.Util
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Divorces a player if a player is married
 *
 * @see MarryPlayer
 *
 * @author sho
 */
class DivorcePlayer : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val player = PlayerHandler.getPlayer(e.author.longID)

        // Player's marry data, in format `player_id&guild_id`
        val playerMarryData = (Objects.requireNonNull<Any>(player.getObject(DataType.MARRY_DATA)) as String).split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        // Makes sure it's just the username and the guild id
        if (playerMarryData.size != 2) {
            messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION)
            return
        }

        // If either are none, return
        if (playerMarryData[0].equals("none", ignoreCase = true) || playerMarryData[1].equals("none", ignoreCase = true)) {
            messageHandler.sendError("You aren't married to anyone!")
            return
        }

        // Gets the guild
        val guild: IGuild
        try {
            guild = e.client.getGuildByID(java.lang.Long.parseLong(playerMarryData[1]))
        } catch (throwaway: NumberFormatException) {
            messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION)
            return
        }

        // Makes sure the guild isn't deleted/doesn't exist
        if (!Util.guildExists(e.client, guild.longID) || guild.isDeleted) {
            messageHandler.sendError("The user wasn't found, divorcing!")
            player.setObject(DataType.MARRY_DATA, "none&none")
            return
        }

        // Gets the user
        val user: IUser
        try {
            user = guild.getUserByID(java.lang.Long.parseLong(playerMarryData[0]))
        } catch (throwaway: NumberFormatException) {
            messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION)
            return
        }

        // Set the divorcee
        player.setObject(DataType.MARRY_DATA, "none&none")

        // Set the divorced player
        PlayerHandler.getPlayer(user.longID).setObject(DataType.MARRY_DATA, "none&none")

        messageHandler.sendEmbed(EmbedBuilder().withDesc("Divorced player `" + user.name + "`."))
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["divorce"] = "Divorce the user you're married to."
        Command.helpCommand(st, "Divorce", e)
    }
}
