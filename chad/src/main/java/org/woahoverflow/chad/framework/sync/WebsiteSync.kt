@file:JvmName("WebsiteSync")

package org.woahoverflow.chad.framework.sync

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.woahoverflow.chad.core.ChadVar
import org.woahoverflow.chad.framework.handle.ArgumentHandler
import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.ThreadHandler
import org.woahoverflow.chad.framework.ui.ChadError
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IGuild
import java.lang.management.ManagementFactory
import java.sql.Connection
import java.sql.DriverManager
import java.time.Instant

val syncLogger: Logger = LoggerFactory.getLogger("Sync")

/**
 * Syncs Chad's data with the website through MySQL
 *
 * @author sho
 */
fun sync(client: IDiscordClient) {
    if (ArgumentHandler.isToggled("DISABLE_EXTERNAL_SYNC")) return

    val connection: Connection

    val start = System.currentTimeMillis()
    syncLogger.debug("Starting website sync...")

    try {
        Class.forName("com.mysql.jdbc.Driver")
        connection = DriverManager.getConnection(JsonHandler["jdbc"])
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

    prepared = connection.prepareStatement("INSERT INTO `chad` (`stats`, `uptime`, `version`, `time`) VALUES (?, ?, ?, ?)")
    prepared.setString(1, "{\"guilds\":{\"amount\":${client.guilds.size},\"biggest\":{\"name\":\"${biggestGuild!!.name}\",\"size\":$biggestGuildSize}},\"users\":{\"amount\":$users},\"shards\":{\"amount\":${client.shardCount}},\"threads\":{\"external\":${ThreadHandler.runningThreads}}}")
    prepared.setLong(2, ManagementFactory.getRuntimeMXBean().uptime)
    prepared.setString(3, ChadVar.VERSION)
    prepared.setLong(4, Instant.now().epochSecond)

    prepared.execute()

    syncLogger.debug("Completed website sync! Took ${System.currentTimeMillis()-start}ms")
}