package org.woahoverflow.chad.framework.handle.xp

import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PlayerHandler
import org.woahoverflow.chad.framework.obj.Player
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser

/**
 * Handles XP stuff
 *
 * @author sho
 */
object XPHandler {
    /**
     * Every two minutes this resets to avoid people spamming messages.
     * After the two minutes, the timer resets and they can send up to 10 messages that will receive xp.
     *
     * Each messages gets a score of 0.1 to 3
     *
     * 0.1 - 1 SCORE = 1XP
     * 2 SCORE = 4XP
     * 3 SCORE = 6XP
     */
    class UserXP internal constructor(val user: IUser, val player: Player) {
        private var lastChatted = System.currentTimeMillis()
        private var amountChatted = 0

        fun registerChat(message: IMessage) {
            if (System.currentTimeMillis() - lastChatted >= 1000*60*2) {
                amountChatted = 0
                lastChatted = System.currentTimeMillis()
            } else if (amountChatted > 10) return

            player.setObject(
                    Player.DataType.XP,
                    player.getObject(Player.DataType.XP) as Long + getMessageScore(message)
            )
            amountChatted++

            val rank = RankHandler.getUserRank(player, refresh = true)

            if (RankHandler.rankUp(player)) {
                val lowerRank = RankHandler.getLowerRank(rank)!!.name
                val currentRank = rank.name
                val aboveRank = RankHandler.aboveRank(player)

                MessageHandler(message.channel, message.author).sendMessage(
                        "You ranked up!\n`$lowerRank` -> `$currentRank`\n\n`$aboveRank` xp til next rank."
                )
            }
        }
    }

    /**
     * Gets a message's score
     *
     * 0.1 - 3.0
     */
    fun getMessageScore(message: IMessage): Int {
        var score = 0.0

        val messageContents = message.content.split(" ")

        for (i in 1..10) {
            if (messageContents.size >= i) {
                score += 0.1
            }

            break
        }

        if (!message.mentions.isEmpty()) score + 1
        if (!message.attachments.isEmpty()) score + 1

        return when {
            1 >= score -> 1
            2 >= score -> 4
            3 >= score -> 6
            else -> 1
        }
    }

    /**
     * Get a user's instance
     */
    fun getUserInstance(user: IUser): UserXP = UserXP(user, PlayerHandler.getPlayer(user.longID))

    /**
     * Gets a user's XP
     */
    fun getUserXP(player: Player): Long = player.getObject(Player.DataType.XP) as Long
}