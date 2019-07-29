@file:JvmName("ChadInstance")

package dev.shog.chad.core

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import dev.shog.chad.core.listener.GuildJoinLeave
import dev.shog.chad.core.listener.OnReady
import dev.shog.chad.core.listener.UserLeaveJoin
import dev.shog.chad.framework.handle.JsonHandler
import dev.shog.chad.framework.handle.coroutine.CoroutineManager
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import kotlin.system.measureTimeMillis
import dev.shog.chad.framework.handle.ArgumentHandler
import dev.shog.chad.framework.handle.dynamo.DynamoDB
import dev.shog.chad.framework.handle.init
import dev.shog.chad.framework.handle.uno.obj.UnoGame
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

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
    if (
            JsonHandler["token"].isEmpty()
            || JsonHandler["uri_link"].isEmpty()
            || JsonHandler["id"].isEmpty()
            || JsonHandler["secret"].isEmpty()
    ) {
        getLogger().error("Bot.json is not filled correctly!")
        exitProcess(1)
    }

    DynamoDB.secret = JsonHandler["secret"]
    DynamoDB.id = JsonHandler["id"]

    getLogger().debug("shoganeko: Chad (${ChadVar.VERSION})")

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