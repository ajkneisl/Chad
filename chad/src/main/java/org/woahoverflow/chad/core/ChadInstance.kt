@file:JvmName("ChadInstance")

package org.woahoverflow.chad.core

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.woahoverflow.chad.core.listener.GuildJoinLeave
import org.woahoverflow.chad.core.listener.OnReady
import org.woahoverflow.chad.core.listener.UserLeaveJoin
import org.woahoverflow.chad.framework.handle.JsonHandler
import org.woahoverflow.chad.framework.handle.coroutine.CoroutineManager
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import kotlin.system.measureTimeMillis
import org.woahoverflow.chad.framework.handle.ArgumentHandler
import org.woahoverflow.chad.framework.handle.init
import java.util.*
import java.util.concurrent.TimeUnit

private var CLIENT: IDiscordClient? = null
private val LOGGER = LoggerFactory.getLogger("Chad Instance")!!
val TIMER = Timer()

/**
 * Gets the Discord Client from [CLIENT], making sure it's initialized.
 */
fun getClient(): IDiscordClient {
    CLIENT.also {
        if (it == null)
            throw Exception("The client hasn't been initialized yet!")

        // Wait until logged in
        while (!it.isLoggedIn) { TimeUnit.MILLISECONDS.sleep(500) }

        return CLIENT!!
    }
}

fun getLogger(): Logger = LOGGER

/**
 * Main function for Chad
 */
fun main(args: Array<String>) {
    JsonHandler.forceCheck()

    // No UI due to servers and stuff
    if (JsonHandler["token"].isEmpty().or(JsonHandler["uri_link"].isEmpty())) {
        getLogger().error("Bot.json is not filled correctly!")
        System.exit(1)
    }

    // Disables MongoDB's logging, as it's just clutter and not really needed
    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF

    getLogger().debug("woahoverflow: Chad (${ChadVar.VERSION})")

    ArgumentHandler.load(args)

    // Initializes client, Discord stuff etc.
    getLogger().also { l ->
        l.info("Initializing client...")

        measureTimeMillis {
            val cli = ClientBuilder().withToken(JsonHandler["token"]).withRecommendedShardCount().build()
            cli.login()
            cli.dispatcher.registerListeners(GuildJoinLeave(), OnReady(), UserLeaveJoin(), CoroutineManager.instance)

            CLIENT = cli
        }.also {
            l.debug("Successfully initialized client! Took $it ms")
        }
    }

    // Found in InitKt
    init()
}