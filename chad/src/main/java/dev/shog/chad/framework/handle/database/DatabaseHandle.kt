package dev.shog.chad.framework.handle.database

import com.mongodb.client.MongoCollection
import org.bson.Document

/**
 * Used for a single operation, such as managing guilds in the actual guild collection
 *
 * @author sho
 */
class DatabaseHandle internal constructor(
        /**
         * The collection to get documents from
         */
        val collection: MongoCollection<Document>,

        /**
         * The key to look for
         */
        private val keyObject: String)
{

    /**
     * Gets an object from a document
     *
     * @param key The object's identifier
     * @param obj The object to get
     * @return The retrieved object
     */
    fun getObject(key: Any, obj: String): Any? {
        val get = collection.find(Document(keyObject, key)).first() ?: return Any()

        return get[obj]
    }

    /**
     * Sets an object from a document
     *
     * @param key The object's identifier
     * @param obj The object to set
     * @param value The new value
     */
    fun setObject(key: Any, obj: String, value: Any) {
        val get = collection.find(Document(keyObject, key)).first() ?: return

        collection.updateOne(get, Document("\$set", Document(obj, value)))
    }

    /**
     * If the object exists in the collection
     *
     * @param key The identifier
     * @return If it exists
     */
    fun documentExists(key: Any): Boolean = collection.find(Document(keyObject, key)).first() != null

    /**
     * Removes a document from the collection
     *
     * @param key The identifier
     */
    fun removeDocument(key: Any) {
        val get = collection.find(Document(keyObject, key)).first() ?: return

        collection.deleteOne(get)
    }
}
