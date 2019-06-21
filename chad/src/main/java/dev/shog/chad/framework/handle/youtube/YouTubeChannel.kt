package dev.shog.chad.framework.handle.youtube

/**
 * A YouTube channel's instance
 */
class YouTubeChannel(
        /**
         * The total view count for a channel
         */
        var viewCount: Long,

        /**
         * The total video count for a channel
         */
        var videoCount: Long,

        /**
         * The subscriber count for a channel
         */
        var subscriberCount: Long,

        /**
         * The channel's username
         */
        val username: String,

        /**
         * The channel's user icon url
         */
        val userIconUrl: String,

        /**
         * The channel's user URL
         */
        val userUrl: String,

        /**
         * The channel's user ID
         */
        val userId: String
) {
    /**
     * Updates a channel's statistics
     */
    fun updateStatistics(viewCount: Long, videoCount: Long, subscriberCount: Long) {
        this.videoCount = videoCount
        this.subscriberCount = subscriberCount
        this.viewCount = viewCount
    }
}