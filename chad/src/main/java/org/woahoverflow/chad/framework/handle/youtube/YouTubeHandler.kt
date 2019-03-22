package org.woahoverflow.chad.framework.handle.youtube

import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.JsonHandler

import java.util.concurrent.ConcurrentHashMap

/**
 * Interaction with YouTube's API
 *
 * @author sho
 */
object YouTubeHandler {
    /**
     * The base URL for API requests
     */
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    /**
     * The cached YouTube channels. This only caches their username and channel data, and their statistical data is reset every new request.
     */
    private val CACHED = ConcurrentHashMap<String, YouTubeChannel>()

    /**
     * Creates a YouTube channel instance with only the user data, not any statistical data.
     */
    private fun getEmptyYouTubeChannel(username: String): YouTubeChannel? {
        val searchUrl = BASE_URL + String.format("search?part=snippet&key=%s&q=%s&type=channel&maxResults=1", ChadVar.YOUTUBE_API_KEY, username)
        val searchResults = JsonHandler.read(searchUrl)
        val profile = searchResults!!.getJSONArray("items").getJSONObject(0).getJSONObject("snippet")

        if (searchResults.getJSONObject("pageInfo").getInt("totalResults") == 0) return null

        val channelUserName = profile.getString("title")
        val channelUserID = profile.getString("channelId")
        val channelUserUrl = "https://youtube.com/channels/$channelUserID"
        val channelUserIconUrl = profile.getJSONObject("thumbnails").getJSONObject("high").getString("url")

        return YouTubeChannel(0, 0, 0, channelUserName, channelUserIconUrl, channelUserUrl, channelUserID)
    }

    /**
     * Updates a YouTube channel's number statistics
     */
    private fun updateStatistics(channel: YouTubeChannel): YouTubeChannel {
        val subscriberUrl = BASE_URL + String.format("channels?key=%s&part=statistics&id=%s", ChadVar.YOUTUBE_API_KEY, channel.userId)
        val statisticResult = JsonHandler.read(subscriberUrl)!!.getJSONArray("items").getJSONObject(0).getJSONObject("statistics")

        val channelSubscriberCount = statisticResult.getString("subscriberCount").toLong()
        val channelViewCount = statisticResult.getString("viewCount").toLong()
        val channelVideoCount = statisticResult.getString("videoCount").toLong()

        channel.updateStatistics(channelViewCount, channelVideoCount, channelSubscriberCount)
        return channel
    }

    /**
     * Gets a YouTube channel
     */
    @JvmStatic
    fun getYoutubeChannel(username: String): YouTubeChannel? {
        // If the API key wasn't set, throw something
        if (!CACHED.containsKey(username)) {
            val channel = getEmptyYouTubeChannel(username)

            if (channel == null)
                return null
            else CACHED[username] = channel
        }

        CACHED[username] = updateStatistics(CACHED[username]!!)

        return CACHED[username]
    }
}
