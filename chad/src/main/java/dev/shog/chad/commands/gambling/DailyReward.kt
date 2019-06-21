package dev.shog.chad.commands.gambling

import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.handle.PlayerHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Command.Class
import dev.shog.chad.framework.obj.Player.DataType
import dev.shog.chad.framework.util.Util
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
            val userBalance = player.getObject(DataType.BALANCE) as Long
            player.setObject(DataType.BALANCE, userBalance + 2000)
            player.setObject(DataType.LAST_DAILY_REWARD, System.currentTimeMillis())
            messageHandler.sendEmbed(EmbedBuilder().withDesc("You claimed your daily reward of `2000`!"))
            return
        }

        // Gets the date of their last daily reward
        val lastDailyReward = player.getObject(DataType.LAST_DAILY_REWARD) as Long

        val difference = Util.howOld(lastDailyReward)
        val day = 24*60*60*1000

        if (difference < day) {
            messageHandler.sendError("You can only claim your reward once a day!\nTime left: `${Util.fancyDate(day - difference)}`")
            return
        }

        player.setObject(DataType.BALANCE, player.getObject(DataType.BALANCE) as Long + 2000)
        player.setObject(DataType.LAST_DAILY_REWARD, System.currentTimeMillis())
        messageHandler.sendEmbed(EmbedBuilder().withDesc("You claimed your daily reward of `2000`!"))
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["dailyreward"] = "Claims your daily reward."
        Command.helpCommand(st, "Daily Reward", e)
    }
}
