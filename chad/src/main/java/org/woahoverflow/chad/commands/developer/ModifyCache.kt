package org.woahoverflow.chad.commands.developer

import org.json.JSONObject
import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.core.ChadVar.eightBallResults
import org.woahoverflow.chad.core.ChadVar.swearWords
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PlayerHandler
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
            val prefix = GuildHandler.handle.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX)

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
                                        PlayerHandler.handle.refreshPlayer(user.longID)
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
                                    GuildHandler.handle.refreshGuild(guild.longID)
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

                            JsonHandler.handle.readArray("https://cdn.woahoverflow.org/data/chad/swears.json")!!.forEach { word -> swearWords.add(word as String) }

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

                            JsonHandler.handle.readArray("https://cdn.woahoverflow.org/data/contributors.json").forEach { v ->
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

                            JsonHandler.handle.readArray("https://cdn.woahoverflow.org/data/chad/8ball.json")!!.forEach { word -> eightBallResults.add(word as String) }

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

                            JsonHandler.handle.readArray("https://cdn.woahoverflow.org/data/chad/presence.json")!!.forEach { v -> ChadVar.presenceRotation.add(v as String) }

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

                            ChadVar.YOUTUBE_API_KEY = JsonHandler.handle.get("youtube_api_key")
                            ChadVar.STEAM_API_KEY = JsonHandler.handle.get("steam_api_key")

                            while (!request.isDone) {
                                TimeUnit.MICROSECONDS.sleep(100)
                            }

                            RequestBuffer.request {
                                message!!.edit("Re-caching `keys`... complete!")
                            }
                            return@Runnable
                        }

                        else -> {
                            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}modcache reset <cache name>")
                        }
                    }
                }

                "view" -> {
                    messageHandler.sendEmbed(EmbedBuilder().withDesc("`users`, `guilds`, `keys`, `swearwords` `8ball`, `contributors`, `presences`"))
                }

                else -> {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}modcache <reset/view> [cache name]")
                }
            }
        }
    }
}