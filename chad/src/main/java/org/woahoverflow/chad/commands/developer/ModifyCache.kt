package org.woahoverflow.chad.commands.developer

import org.json.JSONObject
import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.core.ChadVar.eightBallResults
import org.woahoverflow.chad.core.ChadVar.swearWords
import org.woahoverflow.chad.framework.handle.*
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.ui.UI
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Modifies cache values
 *
 * @author sho
 */
class ModifyCache : Command.Class {
    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["modcache <reset/view> [cache name]"] = "Modifies a database entry."
        return Command.helpCommand(st, "Modify Cache", e)
    }

    override fun run(e: MessageEvent, args: MutableList<String>): Runnable {
        return Runnable {
            val messageHandler = MessageHandler(e.channel, e.author)
            val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX)

            if (args.isEmpty()) {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}modcache <reset/view> [cache name]")
                return@Runnable
            }

            when (args[0].toLowerCase()) {
                "reset" -> {
                    if (args.size != 2) {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}modcache reset <cache name>")
                        return@Runnable
                    }

                    when (args[1].toLowerCase()) {
                        "users" -> {
                            var message: IMessage? = null
                            RequestBuffer.request {
                                message = e.channel.sendMessage("Re-caching `users`...")
                            }

                            val request = RequestBuffer.request {
                                for (guild in e.client.guilds) {
                                    for (user in guild.users) {
                                        PlayerHandler.refreshPlayer(user.longID)
                                    }
                                }
                            }

                            while (!request.isDone) {
                                TimeUnit.SECONDS.sleep(1)
                            }

                            RequestBuffer.request {
                                message!!.edit("Re-caching `users`... complete!")
                            }
                            return@Runnable
                        }

                        "guilds" -> {
                            var message: IMessage? = null
                            RequestBuffer.request {
                                message = e.channel.sendMessage("Re-caching `guilds`...")
                            }

                            val request = RequestBuffer.request {
                                for (guild in e.client.guilds) {
                                    GuildHandler.refreshGuild(guild.longID)
                                }
                            }

                            while (!request.isDone) {
                                TimeUnit.SECONDS.sleep(1)
                            }

                            RequestBuffer.request {
                                message!!.edit("Re-caching `guilds`... complete!")
                            }
                            return@Runnable
                        }

                        "swearwords" -> {
                            var message: IMessage? = null
                            val request = RequestBuffer.request { message = e.channel.sendMessage("Re-caching `swearwords`...") }

                            JsonHandler.readArray("https://cdn.woahoverflow.org/data/chad/swears.json")!!.forEach { word -> swearWords.add(word as String) }

                            while (!request.isDone) {
                                TimeUnit.MICROSECONDS.sleep(100)
                            }

                            RequestBuffer.request {
                                message!!.edit("Re-caching `swearwords`... complete!")
                            }
                            return@Runnable
                        }

                        "contributors" -> {
                            var message: IMessage? = null
                            val request = RequestBuffer.request { message = e.channel.sendMessage("Re-caching `contributors`...") }

                            JsonHandler.readArray("https://cdn.woahoverflow.org/data/contributors.json")!!.forEach { v ->
                                run {
                                    if (java.lang.Boolean.parseBoolean((v as JSONObject).getString("allow"))) {
                                        UI.handle
                                                .addLog("Added user " + v.getString("display_name") + " to group System Administrator", UI.LogLevel.INFO)
                                        ChadVar.DEVELOPERS.add(v.getLong("id"))
                                    } else {
                                        UI.handle.addLog("Avoided adding user " + v.getString("display_name"), UI.LogLevel.INFO)
                                    }
                                }
                            }

                            while (!request.isDone) {
                                TimeUnit.MICROSECONDS.sleep(100)
                            }

                            RequestBuffer.request {
                                message!!.edit("Re-caching `contributors`... complete!")
                            }
                            return@Runnable
                        }

                        "8ball" -> {
                            var message: IMessage? = null
                            val request = RequestBuffer.request { message = e.channel.sendMessage("Re-caching `eightballresults`...") }

                            JsonHandler.readArray("https://cdn.woahoverflow.org/data/chad/8ball.json")!!.forEach { word -> eightBallResults.add(word as String) }

                            while (!request.isDone) {
                                TimeUnit.MICROSECONDS.sleep(100)
                            }

                            RequestBuffer.request {
                                message!!.edit("Re-caching `eightballresults`... complete!")
                            }
                            return@Runnable
                        }

                        "presences" -> {
                            var message: IMessage? = null
                            val request = RequestBuffer.request { message = e.channel.sendMessage("Re-caching `presences`...") }

                            JsonHandler.readArray("https://cdn.woahoverflow.org/data/chad/presence.json")!!.forEach { v -> ChadVar.presenceRotation.add(v as String) }

                            while (!request.isDone) {
                                TimeUnit.MICROSECONDS.sleep(100)
                            }

                            RequestBuffer.request {
                                message!!.edit("Re-caching `presences`... complete!")
                            }
                            return@Runnable
                        }

                        "keys" -> {
                            var message: IMessage? = null
                            val request = RequestBuffer.request { message = e.channel.sendMessage("Re-caching `keys`...") }

                            ChadVar.YOUTUBE_API_KEY = JsonHandler["youtube_api_key"]
                            ChadVar.STEAM_API_KEY = JsonHandler["steam_api_key"]

                            while (!request.isDone) {
                                TimeUnit.MICROSECONDS.sleep(100)
                            }

                            RequestBuffer.request {
                                message!!.edit("Re-caching `keys`... complete!")
                            }
                            return@Runnable
                        }

                        "reddit" -> {
                            var message: IMessage? = null
                            val request = RequestBuffer.request { message = e.channel.sendMessage("Resetting `keys`...") }

                            Reddit.subreddits.clear()

                            while (!request.isDone) {
                                TimeUnit.MICROSECONDS.sleep(100)
                            }

                            RequestBuffer.request {
                                message!!.edit("Resetting `reddit`... complete!")
                            }
                            return@Runnable
                        }

                        else -> {
                            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}modcache reset <cache name>")
                        }
                    }
                }

                "view" -> {
                    if (args.size != 2) {
                        messageHandler.sendEmbed(EmbedBuilder().withDesc("`swearwords`, `8ball`, `contributors`, `presences`, `reddit`"))
                        return@Runnable
                    }

                    when (args[1].toLowerCase()) {
                        "swearwords" -> {
                            val stringBuilder = StringBuilder()
                            for (word in swearWords) stringBuilder.append("`$word`, ")

                            messageHandler.sendMessage("**Swear Words**: " + stringBuilder.toString().removeSuffix(", "))
                            return@Runnable
                        }

                        "8ball" -> {
                            val stringBuilder = StringBuilder()
                            for (word in eightBallResults) stringBuilder.append("`$word`, ")

                            messageHandler.sendMessage("**8Ball**: " + stringBuilder.toString().removeSuffix(", "))
                            return@Runnable
                        }

                        "contributors" -> {
                            val stringBuilder = StringBuilder()
                            for (dev in ChadVar.DEVELOPERS) stringBuilder.append("`$dev`, ")

                            messageHandler.sendMessage("**Contributors**: " + stringBuilder.toString().removeSuffix(", "))
                            return@Runnable
                        }

                        "presences" -> {
                            val stringBuilder = StringBuilder()
                            for (presence in ChadVar.presenceRotation) stringBuilder.append("`$presence`, ")

                            messageHandler.sendMessage("**Presences**: " + stringBuilder.toString().removeSuffix(", "))
                            return@Runnable
                        }

                        "reddit" -> {
                            try {
                                if (Reddit.subreddits.size == 0) {
                                    messageHandler.sendError("No cached Subreddits!")
                                    return@Runnable
                                }

                                val stringBuilder = StringBuilder()
                                for (dev in Reddit.subreddits) {
                                    val hot = dev.value[Reddit.PostType.HOT]
                                    val new = dev.value[Reddit.PostType.NEW]
                                    val top = dev.value[Reddit.PostType.TOP]

                                    var built = "`${dev.key}` ["

                                    if (hot != null) built += "HOT: `${dev.value[Reddit.PostType.HOT]!!.size}`, "
                                    if (new != null) built += "NEW: `${dev.value[Reddit.PostType.NEW]!!.size}`, "
                                    if (top != null) built += "TOP: `${dev.value[Reddit.PostType.TOP]!!.size}`, "

                                    built = built.removeSuffix(", ") + "], "

                                    stringBuilder.append(built)
                                }

                                messageHandler.sendMessage("**Reddit**: " + stringBuilder.toString().removeSuffix(", "))
                                return@Runnable
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        }

                        else -> {
                            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}modcache view <cache name>")
                        }
                    }
                }

                else -> {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}modcache <reset/view> [cache name]")
                }
            }
        }
    }
}