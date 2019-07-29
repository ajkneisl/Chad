package dev.shog.chad.framework.handle.database

import dev.shog.chad.framework.handle.dynamo.DynamoDB

/**
 * Accesses the database
 *
 * @author sho
 */
object DatabaseManager {
    private val client = DynamoDB.create() ?: throw Exception("Couldn't get DynamoDB client!")

    /**
     * Gets a table.
     *
     * @param table The requested table
     * @return The retrieved table
     */
    private fun getTable(table: String) = client.getTable(table) ?: throw Exception("Couldn't retrieve table $table")

    /**
     * Handle for User Data
     */
    val USER_DATA = DatabaseHandle(getTable("ch-users"), "id")

    /**
     * Handle for Guild Data
     */
    val GUILD_DATA = DatabaseHandle(getTable("ch-guilds"), "id")
}
