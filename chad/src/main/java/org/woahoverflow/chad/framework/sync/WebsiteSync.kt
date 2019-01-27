package org.woahoverflow.chad.framework.sync

import org.woahoverflow.chad.core.ChadInstance
import org.woahoverflow.chad.framework.Chad
import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.ui.ChadError
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IGuild
import java.lang.management.ManagementFactory
import java.sql.Connection
import java.sql.DriverManager


fun sync(client: IDiscordClient) {
    val connection: Connection

    ChadInstance.getLogger().debug("Starting website sync...")

    try {
        Class.forName("com.mysql.jdbc.Driver")
        connection = DriverManager.getConnection(JsonHandler.handle.get("jdbc"))
    } catch (ex: Exception) {
        ChadError.throwError("Couldn't connect to database!", ex)
        return
    }

    var prepared = connection.prepareStatement("DELETE FROM `chad`")
    prepared.execute()

    val guilds = client.guilds

    var biggestGuild: IGuild? = null
    var biggestGuildSize = 0

    var users = 0
    for (guild in guilds) {
        users += guild.users.size

        if (guild.users.size > biggestGuildSize) {
            biggestGuild = guild
            biggestGuildSize = guild.users.size
        }
    }

    prepared = connection.prepareStatement("INSERT INTO `chad` (`stats`, `uptime`, `version`) VALUES (?, ?, ?)")
    prepared.setString(1, "{\"guilds\":{\"amount\":${client.guilds.size},\"biggest\":{\"name\":\"${biggestGuild!!.name}\",\"size\":$biggestGuildSize}},\"users\":{\"amount\":$users},\"shards\":{\"amount\":${client.shardCount}},\"threads\":{\"local\":${Chad.internalRunningThreads},\"external\":${Chad.runningThreads}}}")
    prepared.setLong(2, ManagementFactory.getRuntimeMXBean().uptime)
    prepared.setString(3, "v0.8.0")

    prepared.execute()

    ChadInstance.getLogger().debug("Successfully executed website sync!")
}