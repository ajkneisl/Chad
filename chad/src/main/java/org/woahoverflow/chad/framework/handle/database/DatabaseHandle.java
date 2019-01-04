package org.woahoverflow.chad.framework.handle.database;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Used for a single operation, such as managing guilds in the actual guild collection
 *
 * @author sho
 */
public class DatabaseHandle {
    /**
     * The collection to get documents from
     */
    public final MongoCollection<Document> collection;

    /**
     * The key to look for
     */
    private final String keyObject;

    /**
     * Local Constructor
     *
     * @param collection The collection to create the handle on
     * @param keyObject The key to look for
     */
    DatabaseHandle(MongoCollection<Document> collection, String keyObject) {
        this.collection = collection;
        this.keyObject = keyObject;
    }

    /**
     * Gets an object from a document
     *
     * @param key The object's identifier
     * @param object The object to get
     * @return The retrieved object
     */
    public final synchronized Object getObject(Object key, String object) {
        Document get = collection.find(new Document(keyObject, key)).first();

        if (get == null)
            return new Object();

        return get.get(object);
    }

    /**
     * Sets an object from a document
     *
     * @param key The object's identifier
     * @param object The object to set
     * @param value The new value
     */
    public final synchronized void setObject(Object key, String object, Object value) {
        Document get = collection.find(new Document(keyObject, key)).first();

        if (get == null)
            return;

        collection.updateOne(get, new Document("$set", new Document(object, value)));
    }

    /**
     * If the object exists in the collection
     *
     * @param key The identifier
     * @return If it exists
     */
    public final synchronized boolean documentExists(Object key) {
        return !(collection.find(new Document(keyObject, key)).first() == null);
    }

    /**
     * Removes a document from the collection
     *
     * @param key The identifier
     */
    public final synchronized void removeDocument(Object key) {
        Document get = collection.find(new Document(keyObject, key)).first();

        if (get == null)
            return;

        collection.deleteOne(get);
    }
}
