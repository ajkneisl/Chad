package dev.shog.chad.commands.info

import dev.shog.chad.framework.handle.GuildHandler
import dev.shog.chad.framework.handle.MessageHandler
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Gets info about the current guild
 *
 * @author sho
 */
class GuildInfo : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        // Creates an embed builder and applies the title
        val embedBuilder = EmbedBuilder()

        // Gets the human to bot.
        var human = 0
        var bot = 0
        for (u in e.guild.users) {
            if (!u.isBot)
                human++
            else
                bot++
        }

        val guild = GuildHandler.getGuild(e.guild.longID)

        // Applies the description
        embedBuilder.withDesc(
                "**Name** : `${e.guild.name}`" +
                        "\n**Owner** : `${e.guild.owner.name}`" +
                        "\n**Role Amount** : `${e.guild.roles.size}`" +
                        "\n**Human to Bots** : `$human/$bot`"+
                        "\n**User Amount** : `${e.guild.users.size}`" +
                        "\n**Voice Channels** : `${e.guild.voiceChannels.size}`" +
                        "\n**Text Channels** : `${e.guild.channels.size}`" +
                        "\n**Categories** : `${e.guild.categories.size}`" +
                        "\n**Creation Date** : `${SimpleDateFormat("MM/dd/yyyy").format(Date.from(e.guild.creationDate))}`" +
                        "\n\n**Messages Sent** : `${guild.getObject(Guild.DataType.MESSAGES_SENT)}`" +
                        "\n\n**Commands Sent** : `${guild.getObject(Guild.DataType.COMMANDS_SENT)}`"
        )

        // Adds the guild's image and sends.
        embedBuilder.withImage(e.guild.iconURL)
        MessageHandler(e.channel, e.author).sendEmbed(embedBuilder)
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["guildinfo"] = "Gets information about the guild."
        Command.helpCommand(st, "Guild Info", e)
    }
}
