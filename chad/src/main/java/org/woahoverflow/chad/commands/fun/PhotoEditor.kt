package org.woahoverflow.chad.commands.`fun`

import org.json.JSONObject
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.util.EmbedBuilder

import java.net.MalformedURLException
import java.net.URL
import java.util.HashMap
import java.util.Objects

/**
 * Use preset options to modify a photo
 *
 * @author sho, codebasepw
 */
class PhotoEditor : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX) as String

        // Makes sure the user has attached a file
        if (e.message.attachments.isEmpty()) {
            messageHandler.sendError("No file was found!")
            return
        }

        // Makes sure they added arguments
        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "pe [deepfry]")
            return
        }

        // Makes sure the attachment is a PNG or JPG
        if (!(e.message.attachments[0].url.endsWith(".png") || e.message.attachments[0].url.endsWith(".jpg"))) {
            MessageHandler(e.channel, e.author).sendError("Invalid Format!\nPlease use PNG or JPG")
            return
        }

        // Assigns the URL to the attachment's URL
        val url: URL
        try {
            url = URL(e.message.attachments[0].url)
        } catch (e1: MalformedURLException) {
            messageHandler.sendPresetError(MessageHandler.Messages.INTERNAL_EXCEPTION)
            return
        }


        // Deepfry
        if (args[0].equals("deepfry", ignoreCase = true)) {
            messageHandler.sendEmbed(EmbedBuilder().withImage(
                    Objects.requireNonNull<JSONObject>(JsonHandler.read("https://nekobot.xyz/api/imagegen?type=deepfry&image=$url")).getString("message"))
            )
            return
        }

        // If none of the arguments were met, return;
        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, prefix + "pe [deepfry]")
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["pe deepfry [image]"] = "Deepfries an image."
        Command.helpCommand(st, "Photo Editor", e)
    }
}
