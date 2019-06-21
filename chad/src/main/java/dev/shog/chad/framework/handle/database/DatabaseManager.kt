package dev.shog.chad.framework.handle.database

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import dev.shog.chad.framework.handle.JsonHandler

/**
 * Accesses the database
 *
 * @author sho, codebasepw
 */
object DatabaseManager {
    private val client: MongoClient = MongoClient(MongoClientURI(JsonHandler["uri_link"]))
    private val database: MongoDatabase = client.getDatabase("Database")

    /**
     * Gets a separate collection from the main.
     *
     * @param colName The requested collection
     * @return The retrieved collection
     */
    private fun getSeparateCollection(colName: String) = DatabaseHandle(database.getCollection(colName), "id")

    /**
     * Handle for User Data
     */
    val USER_DATA = getSeparateCollection("user_data")

    /**
     * Handle for Guild Data
     */
    val GUILD_DATA = getSeparateCollection("guild_data")
}
