package org.woahoverflow.chad.framework.handle;

import org.json.JSONObject;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.framework.ui.ChadError;

/**
 * Interaction with YouTube's API
 *
 * @author sho
 */
public final class YouTubeHandler {
    /**
     * An instance of a retrieved channel
     */
    public static final class Channel {
        /**
         * The channels total view count
         */
        public long viewCount;

        /**
         * The amount of videos the channel has
         */
        public long videoCount;

        /**
         * The amount of subscribers the channel has
         */
        public long subscriberCount;

        /**
         * The channel's username
         */
        public String username;

        /**
         * The user's icon url
         */
        public String userIconUrl;

        /**
         * The user's url
         */
        public String userUrl;

        /**
         * Build's the channel's default
         *
         * @param username The channel's username
         * @param userIconUrl The channel's icon url
         * @param userUrl The channel's url
         * @return The same instance
         */
        Channel buildDefault(String username, String userIconUrl, String userUrl) {
            this.username = username;
            this.userUrl = userUrl;
            this.userIconUrl = userIconUrl;

            return this;
        }

        /**
         * Build's the channel's statistics
         *
         * @param subscriberCount The channel's subscriber count
         * @param viewCount The channel's view count
         * @param videoCount The channel's video count
         * @return The same instance
         */
        Channel buildStatistics(long subscriberCount, long viewCount, long videoCount) {
            this.videoCount = videoCount;
            this.viewCount = viewCount;
            this.subscriberCount = subscriberCount;

            return this;
        }
    }
    /**
     * The base URL for API requests
     */
    private static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";

    /**
     * Builds a YouTube channel with statistics
     *
     * @param username The channel to search for
     * @return If a channel was found, return the channel
     */
    public static Channel getYoutubeChannel(String username) {
        // If the API key wasn't set, throw something
        if (ChadVar.YOUTUBE_API_KEY == null) {
            ChadError.throwError("YouTube API Key wasn't set!");
            return null;
        }

        // The URL to search for the channel
        final String searchUrl = BASE_URL + String.format("search?part=snippet&key=%s&q=%s&type=channel&maxResults=1", ChadVar.YOUTUBE_API_KEY, username);
        JSONObject searchResults = JsonHandler.INSTANCE.read(searchUrl);
        JSONObject profile = searchResults.getJSONArray("items").getJSONObject(0).getJSONObject("snippet");

        // If it didn't find anything
        if (searchResults.getJSONObject("pageInfo").getInt("totalResults") == 0)
            return null;

        // Gets the username, user id, user url, and the icon url
        String channelUserName = profile.getString("title");
        String channelUserID = profile.getString("channelId");
        String channelUserUrl = "https://youtube.com/channels/" + channelUserID;
        String channelUserIconUrl = profile.getJSONObject("thumbnails").getJSONObject("high")
            .getString("url");

        // The URL to search for the channel's statistics
        final String subscriberUrl = BASE_URL + String.format("channels?key=%s&part=statistics&id=%s", ChadVar.YOUTUBE_API_KEY, channelUserID);
        JSONObject statisticResult = JsonHandler.INSTANCE.read(subscriberUrl).getJSONArray("items").getJSONObject(0).getJSONObject("statistics");

        // Gets the subscriber count, view count and the video count.
        long channelSubscriberCount = Long.parseLong(statisticResult.getString("subscriberCount"));
        long channelViewCount = Long.parseLong(statisticResult.getString("viewCount"));
        long channelVideoCount = Long.parseLong(statisticResult.getString("videoCount"));

        // Returns the built channel
        return new Channel().buildDefault(channelUserName, channelUserIconUrl, channelUserUrl).buildStatistics(channelSubscriberCount, channelViewCount, channelVideoCount);
    }
}
