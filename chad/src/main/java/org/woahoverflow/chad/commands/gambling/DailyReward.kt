package org.woahoverflow.chad.commands.gambling

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PlayerHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Command.Class
import org.woahoverflow.chad.framework.obj.Player.DataType
import org.woahoverflow.chad.framework.util.Util
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.util.*

/**
 * Gets a daily reward of 'money'
 *
 * @author sho
 */
class DailyReward : Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val player = PlayerHandler.getPlayer(e.author.longID)

        // If the user hasn't claimed the daily reward ever
        if (player.getObject(DataType.LAST_DAILY_REWARD) == "none") {
            // Get the user's current balance
            val userBalance = player.getObject(DataType.BALANCE) as Long

            // Adds the money
            player.setObject(DataType.BALANCE, userBalance + 2000)

            // Updates the user's ldr to the current time
            player.setObject(DataType.LAST_DAILY_REWARD, System.currentTimeMillis())

            // Send the message
            messageHandler.sendEmbed(EmbedBuilder().withDesc("You claimed your daily reward of `2000`!"))
            return
        }

        // Gets the date of their last daily reward
        val lastDailyReward = player.getObject(DataType.LAST_DAILY_REWARD) as Long // TODO

        val difference = Util.howOld(lastDailyReward)
        val day = 24 * 60 * 60 * 1000

        if (difference < day) {
            messageHandler.sendError("You can only claim your reward once a day!\nTime left: " + Util.fancyDate(day - difference))
            return
        }

        // Get the user's current balance
        val currentBalance = player.getObject(DataType.BALANCE) as Long

        // Adds the money
        player.setObject(DataType.BALANCE, currentBalance + 2000)

        // Updates the user's ldr
        player.setObject(DataType.LAST_DAILY_REWARD, System.currentTimeMillis())

        // Send the message
        messageHandler.sendEmbed(EmbedBuilder().withDesc("You claimed your daily reward of `2000`!"))
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["dailyreward"] = "Claims your daily reward."
        Command.helpCommand(st, "Daily Reward", e)
    }
}
