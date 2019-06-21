package dev.shog.chad.commands.developer

import kotlinx.coroutines.delay
import org.json.JSONObject
import dev.shog.chad.core.ChadVar
import dev.shog.chad.core.ChadVar.swearWords
import dev.shog.chad.core.getLogger
import dev.shog.chad.framework.handle.*
import dev.shog.chad.framework.obj.Command
import dev.shog.chad.framework.obj.Guild
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.RequestBuffer
import java.util.*

/**
 * Modifies cache values
 *
 * @author sho
 */
class ModifyCache : Command.Class {
    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["modcache <reset/view> [cache name]"] = "Modifies a database entry."
        Command.helpCommand(st, "Modify Cache", e)
    }

    override suspend fun run(e: MessageEvent, args: MutableList<String>) {
        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX)

        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}modcache [reset/view] [cache name]")
            return
        }

        when (args[0].toLowerCase()) {
            "reset" -> {
                if (args.size != 2) {
                    messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}modcache reset [cache name]")
                    return
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
                            delay(1000L)
                        }

                        RequestBuffer.request {
                            message!!.edit("Re-caching `users`... complete!")
                        }
                        return
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
                            delay(1000L)
                        }

                        RequestBuffer.request {
                            message!!.edit("Re-caching `guilds`... complete!")
                        }
                        return
                    }

                    "swearwords" -> {
                        SwearWordEngine.update()

                        RequestBuffer.request {
                            e.channel.sendMessage("Re-caching `swearwords`... complete!")
                        }
                        return
                    }

                    "presences" -> {
                        PresenceHandler.refreshPresences()

                        RequestBuffer.request {
                            messageHandler.sendMessage("Recached `presences`!")
                        }
                        return
                    }

                    "keys" -> {
                        var message: IMessage? = null
                        val request = RequestBuffer.request { message = e.channel.sendMessage("Re-caching `keys`...") }

                        ChadVar.YOUTUBE_API_KEY = JsonHandler["youtube_api_key"]
                        ChadVar.STEAM_API_KEY = JsonHandler["steam_api_key"]

                        while (!request.isDone) {
                            delay(100L)
                        }

                        RequestBuffer.request {
                            message!!.edit("Re-caching `keys`... complete!")
                        }
                        return
                    }

                    "reddit" -> {
                        var message: IMessage? = null
                        val request = RequestBuffer.request { message = e.channel.sendMessage("Resetting `keys`...") }

                        Reddit.subreddits.clear()

                        while (!request.isDone) {
                            delay(100L)
                        }

                        RequestBuffer.request {
                            message!!.edit("Resetting `reddit`... complete!")
                        }
                        return
                    }

                    else -> {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}modcache reset [cache name]")
                    }
                }
            }

            "view" -> {
                if (args.size != 2) {
                    messageHandler.sendEmbed(EmbedBuilder().withDesc("`swearwords`, `presences`, `reddit`"))
                    return
                }

                when (args[1].toLowerCase()) {
                    "swearwords" -> {
                        val stringBuilder = StringBuilder()
                        for (word in swearWords) stringBuilder.append("`$word`, ")

                        messageHandler.sendMessage("**Swear Words**: " + stringBuilder.toString().removeSuffix(", "))
                        return
                    }

                    "presences" -> {
                        val stringBuilder = StringBuilder()
                        for (s in PresenceHandler.presences) stringBuilder.append("Activity Type: `${s.activityType}`, Status Type: `${s.statusType}`, Status: `${s.status}`\n")

                        messageHandler.sendMessage("**Presences**: " + stringBuilder.toString().removeSuffix("\n"))
                        return
                    }

                    "reddit" -> {
                        try {
                            if (Reddit.subreddits.size == 0) {
                                messageHandler.sendError("No cached Subreddits!")
                                return
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
                            return
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    else -> {
                        messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}modcache reset [cache name]")
                    }
                }
            }

            else -> {
                messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}modcache [reset/view] [cache name]")
            }
        }
    }
}