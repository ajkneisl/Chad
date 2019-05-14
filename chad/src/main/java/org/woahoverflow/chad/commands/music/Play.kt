package org.woahoverflow.chad.commands.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.woahoverflow.chad.core.ChadVar.playerManager
import org.woahoverflow.chad.framework.handle.GuildHandler
import org.woahoverflow.chad.framework.handle.MessageHandler
import org.woahoverflow.chad.framework.handle.PermissionHandler
import org.woahoverflow.chad.framework.handle.getMusicManager
import org.woahoverflow.chad.framework.obj.Command
import org.woahoverflow.chad.framework.obj.Guild
import org.woahoverflow.chad.framework.util.Util
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.RequestBuffer
import java.util.*

/**
 * Unpause guild's music
 *
 * @author sho
 */
class Play : Command.Class {
    override suspend fun run(e: MessageEvent, args: MutableList<String>) {

        val messageHandler = MessageHandler(e.channel, e.author)
        val prefix = GuildHandler.getGuild(e.guild.longID).getObject(Guild.DataType.PREFIX)

        // If the bot needs to join the music channel
        val userChannel = e.author.getVoiceStateForGuild(e.guild).channel
        val chadChannel = e.client.ourUser.getVoiceStateForGuild(e.guild).channel

        if (args.isEmpty()) {
            messageHandler.sendPresetError(MessageHandler.Messages.INVALID_ARGUMENTS, "${prefix}play [song name]")
            return
        }

        if (userChannel == null) {
            messageHandler.sendError("You're not in a channel!")
            return
        }

        val join = chadChannel == null

        // If the user isn't in the same channel, and Chad is currently playing something don't join
        if (userChannel != chadChannel && !join) {
            messageHandler.sendError("You're not in the same channel as Chad!")
            return
        }

        val hasPermission = RequestBuffer.request<Boolean> {
            userChannel.getModifiedPermissions(e.client.ourUser).contains(Permissions.VOICE_CONNECT)
        }.get()

        if (!hasPermission && join) {
            messageHandler.sendError("I don't have permission to join ${userChannel.name}!")
            return
        } else if (join) userChannel.join()

        // The guild's music manager
        val manager = getMusicManager(e.guild, userChannel)

        // Builds the music name
        var string = ""

        for (arg in args) string += "$arg "

        string = string.trim()

        // If they're using a supported url
        if (Util.startsWith(string, "https://www.youtube.com/watch?v=", "https://youtube.com/watch?v=", "http://youtube.com/watch?v=", "https://soundcloud.com/",  "https://soundcloud.com/")) {
            playerManager.loadItemOrdered(manager, string,
                    object : AudioLoadResultHandler {
                        override fun trackLoaded(track: AudioTrack) {
                            if (join) userChannel.join()

                            manager.scheduler.queue(track)
                            manager.setActive(true)

                            messageHandler.sendMessage(
                                    String.format("Now playing `%s` by `%s` [`%s`]\n%s", track.info.title, track.info.author, Util.fancyDate(track.info.length), track.info.uri)
                            )
                        }

                        // When something is searched
                        override fun playlistLoaded(playlist: AudioPlaylist) {
                            if (playlist.tracks.size > 100 && !PermissionHandler.isDeveloper(e.author)) {
                                messageHandler.sendError("Please don't play playlists with more than 100 songs in them!")
                                return
                            }

                            if (playlist.tracks.size == 0) {
                                messageHandler.sendError("Please don't play playlists with no songs in them!")
                                return
                            }

                            val builder = StringBuilder()
                            var removed = false

                            var loc = 0
                            for (track in playlist.tracks) {
                                if (track.duration > 60*1000*60*2 && !PermissionHandler.isDeveloper(e.author)) {
                                    removed = true
                                    continue
                                }

                                if (loc <= 10) {
                                    builder.append("`${track.info.title}`, ")
                                    loc++
                                } else if (loc == 11) {
                                    builder.append("... and `${playlist.tracks.size - 11}` more.")
                                    loc++
                                }

                                manager.scheduler.queue(track)
                                manager.setActive(true)
                            }

                            if (join) userChannel.join()

                            if (removed) {
                                messageHandler.sendMessage(
                                        String.format("One or more songs have not been queued due to their length.\n\nQueued playlist `%s` [`%s` tracks]\n\nIncludes %s", playlist.name, playlist.tracks.size, builder.toString())
                                )
                                return
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
                            messageHandler.sendError("There was no results for `$string`!")
                        }
                    })
            return
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

                        if (join) userChannel.join()

                        val track = playlist.tracks[0]

                        if (track.duration > 60*1000*60*2 && !PermissionHandler.isDeveloper(e.author)) {
                            messageHandler.sendError("Please play songs that are under 2 hours!")
                            return
                        }

                        manager.scheduler.queue(track)
                        manager.setActive(true)

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
                        messageHandler.sendError("There was no results for `$string`!")
                    }
                })
    }

    override suspend fun help(e: MessageEvent) {
        val st = HashMap<String, String>()
        st["play [song name]"] = "Play music."
        Command.helpCommand(st, "Play", e)
    }
}
