package org.woahoverflow.chad.commands.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.vdurmont.emoji.EmojiManager
import org.woahoverflow.chad.core.ChadVar.playerManager
import org.woahoverflow.chad.framework.Chad
import org.woahoverflow.chad.framework.Util
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.RequestBuilder
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Unpause guild's music
 *
 * @author sho
 */
class Play : Command.Class {
    override fun run(e: MessageReceivedEvent, args: List<String>): Runnable {
        return Runnable {
            try {
                val messageHandler = MessageHandler(e.channel, e.author)

                // The guild's music manager
                val manager = Chad.getMusicManager(e.guild)

                // If the bot needs to join the music channel
                val userChannel = e.author.getVoiceStateForGuild(e.guild).channel
                val chadChannel = e.client.ourUser.getVoiceStateForGuild(e.guild).channel

                if (userChannel == null) {
                    messageHandler.sendError("You're not in a channel!")
                    return@Runnable
                }

                val join = chadChannel == null

                // If the user isn't in the same channel, and Chad is currently playing something don't join
                if (userChannel != chadChannel && !join) {
                    messageHandler.sendError("You're not in the same channel as Chad!")
                    return@Runnable
                }

                // Builds the music name
                var string = ""

                for (arg in args) {
                    string += "$arg "
                }

                string = string.trim()
                println(string)

                // If they're using a supported url
                if (string.startsWith("https://youtube.com") || string.startsWith("https://soundcloud.com")) {
                    playerManager.loadItemOrdered(manager, string,
                            object : AudioLoadResultHandler {
                                override fun trackLoaded(track: AudioTrack) {
                                    if (join) {
                                        userChannel.join()
                                    }

                                    manager.scheduler.queue(track)

                                    messageHandler.sendMessage(
                                            String.format("Now playing `%s` by `%s` [`%s`]\n%s", track.info.title, track.info.author, Util.fancyDate(track.info.length), track.info.uri)
                                    )
                                }

                                // When something is searched
                                override fun playlistLoaded(playlist: AudioPlaylist) {
                                    if (playlist.tracks.size > 100) {
                                        messageHandler.sendError("Please don't play playlists with more than 100 songs in them!")
                                        return
                                    }

                                    if (playlist.tracks.size == 0) {
                                        messageHandler.sendError("Please don't play playlists with no songs in them!")
                                        return
                                    }

                                    val builder = StringBuilder()

                                    var loc = 0
                                    for (track in playlist.tracks) {
                                        if (loc <= 10) {
                                            builder.append("`${track.info.title}`, ")
                                            loc++
                                        } else if (loc == 11) {
                                            builder.append("... and `${playlist.tracks.size - 11}` more.")
                                            loc++
                                        }

                                        manager.scheduler.queue(track)
                                    }

                                    if (join) {
                                        userChannel.join()
                                    }

                                    messageHandler.sendMessage(
                                            String.format("Queued playlist `%s` [`%s` tracks]\n\nIncludes %s", playlist.name, playlist.tracks.size, builder.toString())
                                    )
                                }

                                override fun noMatches() {
                                    messageHandler.sendError("There was no results for $string!")
                                }

                                // If there's an exception
                                override fun loadFailed(exception: FriendlyException) {
                                    exception.printStackTrace()
                                    // something failed, we'll only see and they'll just receive that there was no matches
                                }
                            })
                    return@Runnable
                }

                // If they didn't include a supported URL, search it on youtube
                playerManager.loadItemOrdered(manager, "ytsearch:$string",
                        object : AudioLoadResultHandler {
                            override fun trackLoaded(track: AudioTrack) {
                                // this currently isn't used
                            }

                            // When something is searched
                            override fun playlistLoaded(playlist: AudioPlaylist) {
                                if (playlist.tracks.size < 5) {
                                    messageHandler.sendError("There was no results for `$string`!")
                                    return
                                }

                                val builder = StringBuilder()

                                val it = playlist.tracks.iterator()

                                var location = 1

                                while (it.hasNext() && location <= 5) {
                                    val track = it.next()
                                    builder.append("`$location`. `${track.info.title}` [`${Util.fancyDate(track.info.length)}`]\n")

                                    location++
                                }

                                // The message to be sent
                                var message: IMessage? = null

                                val requestBuilder = RequestBuilder(e.client)

                                requestBuilder.shouldBufferRequests(true)

                                requestBuilder.doAction {
                                    message = e.channel.sendMessage(builder.toString().removeSuffix("\n"))
                                    true
                                }.andThen {
                                    message!!.addReaction(EmojiManager.getForAlias("one"))
                                    true
                                }.andThen {
                                    message!!.addReaction(EmojiManager.getForAlias("two"))
                                    true
                                }.andThen {
                                    message!!.addReaction(EmojiManager.getForAlias("three"))
                                    true
                                }.andThen {
                                    message!!.addReaction(EmojiManager.getForAlias("four"))
                                    true
                                }.andThen {
                                    message!!.addReaction(EmojiManager.getForAlias("five"))
                                    true
                                }.execute()

                                var chose = false

                                var reactionOne = false
                                var reactionTwo = false
                                var reactionThree = false
                                var reactionFour = false
                                var reactionFive = false

                                var timeout = 0

                                while (!chose) {
                                    timeout++

                                    if (timeout >= 10) {
                                        messageHandler.sendError("Timed out!")
                                        return
                                    }

                                    TimeUnit.SECONDS.sleep(1)

                                    val reactionBuilder = RequestBuilder(e.client)

                                    requestBuilder.shouldBufferRequests(true)

                                    reactionBuilder.doAction {
                                        reactionFive = message!!.getReactionByUnicode(EmojiManager.getForAlias("five")).getUserReacted(e.author)
                                        chose = true
                                        true
                                    }.andThen {
                                        reactionFour = message!!.getReactionByUnicode(EmojiManager.getForAlias("four")).getUserReacted(e.author)
                                        chose = true
                                        true
                                    }.andThen {
                                        reactionThree = message!!.getReactionByUnicode(EmojiManager.getForAlias("three")).getUserReacted(e.author)
                                        chose = true
                                        true
                                    }.andThen {
                                        reactionTwo = message!!.getReactionByUnicode(EmojiManager.getForAlias("two")).getUserReacted(e.author)
                                        chose = true
                                        true
                                    }.andThen {
                                        reactionOne = message!!.getReactionByUnicode(EmojiManager.getForAlias("one")).getUserReacted(e.author)
                                        chose = true
                                        true
                                    }.execute()
                                }

                                if (reactionOne) {
                                    if (join) {
                                        userChannel.join()
                                    }

                                    val track = playlist.tracks[0]

                                    manager.scheduler.queue(track)

                                    messageHandler.sendMessage(
                                            String.format("Now playing `%s` by `%s` [`%s`]\n%s", track.info.title, track.info.author, Util.fancyDate(track.info.length), track.info.uri)
                                    )

                                    return
                                }

                                if (reactionTwo) {
                                    if (join) {
                                        userChannel.join()
                                    }

                                    val track = playlist.tracks[1]

                                    manager.scheduler.queue(track)

                                    messageHandler.sendMessage(
                                            String.format("Now playing `%s` by `%s` [`%s`]\n%s", track.info.title, track.info.author, Util.fancyDate(track.info.length), track.info.uri)
                                    )

                                    return
                                }

                                if (reactionThree) {
                                    if (join) {
                                        userChannel.join()
                                    }

                                    val track = playlist.tracks[2]

                                    manager.scheduler.queue(track)

                                    messageHandler.sendMessage(
                                            String.format("Now playing `%s` by `%s` [`%s`]\n%s", track.info.title, track.info.author, Util.fancyDate(track.info.length), track.info.uri)
                                    )

                                    return
                                }

                                if (reactionFour) {
                                    if (join) {
                                        userChannel.join()
                                    }

                                    val track = playlist.tracks[3]

                                    manager.scheduler.queue(track)

                                    messageHandler.sendMessage(
                                            String.format("Now playing `%s` by `%s` [`%s`]\n%s", track.info.title, track.info.author, Util.fancyDate(track.info.length), track.info.uri)
                                    )

                                    return
                                }

                                if (reactionFive) {
                                    if (join) {
                                        userChannel.join()
                                    }

                                    val track = playlist.tracks[4]

                                    manager.scheduler.queue(track)

                                    messageHandler.sendMessage(
                                            String.format("Now playing `%s` by `%s` [`%s`]\n%s", track.info.title, track.info.author, Util.fancyDate(track.info.length), track.info.uri)
                                    )

                                    return
                                }
                            }

                            override fun noMatches() {
                                messageHandler.sendError("There was no results for `$string`!")
                            }

                            // If there's an exception
                            override fun loadFailed(exception: FriendlyException) {
                                exception.printStackTrace()
                                // something failed, we'll only see and they'll just receive that there was no matches
                            }
                        })
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun help(e: MessageReceivedEvent): Runnable {
        val st = HashMap<String, String>()
        st["play"] = "Un-Pause music."
        return Command.helpCommand(st, "Play", e)
    }
}
