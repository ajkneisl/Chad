package org.woahoverflow.chad.commands.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.woahoverflow.chad.core.ChadVar.playerManager
import org.woahoverflow.chad.framework.Util
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.getMusicManager
import org.woahoverflow.chad.framework.obj.Command
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import java.util.*

/**
 * Unpause guild's music
 *
 * @author sho
 */
class Play : Command.Class {
    override fun run(e: MessageEvent, args: List<String>): Runnable {
        return Runnable {
            val messageHandler = MessageHandler(e.channel, e.author)

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

            if (join)
                userChannel.join()


            // The guild's music manager
            val manager = getMusicManager(e.guild, userChannel)

            // Builds the music name
            var string = ""

            for (arg in args) {
                string += "$arg "
            }

            string = string.trim()

            // If they're using a supported url
            if (string.startsWith("https://youtube.com") || string.startsWith("https://soundcloud.com") || string.startsWith("http://youtube.com") || string.startsWith("http://soundcloud.com")) {
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
                            if (playlist.tracks.size == 0) {
                                messageHandler.sendError("There was no results for `$string`!")
                                return
                            }

                            if (join) {
                                userChannel.join()
                            }

                            val track = playlist.tracks[0]

                            manager.scheduler.queue(track)

                            messageHandler.sendMessage(
                                    String.format("Now playing `%s` by `%s` [`%s`]\n%s", track.info.title, track.info.author, Util.fancyDate(track.info.length), track.info.uri)
                            )
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
        }
    }

    override fun help(e: MessageEvent): Runnable {
        val st = HashMap<String, String>()
        st["play"] = "Play music."
        return Command.helpCommand(st, "Play", e)
    }
}
