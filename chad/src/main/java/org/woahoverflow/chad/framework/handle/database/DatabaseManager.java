package org.woahoverflow.chad.framework.handle.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.woahoverflow.chad.framework.handle.JsonHandler;

/**
 * Accesses the database
 *
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class DatabaseManager
{

    /**
     * The global handle for the Database
     */
    public static final DatabaseManager handle = new DatabaseManager();

    /**
     * The Mongo Client
     */
    private final MongoClient cli;

    /**
     * The Mongo Collection
     */
    private final MongoCollection<Document> col;

    /**
     * The Mongo Database
     */
    private final MongoDatabase db;

    /**
     * Handle for User Data
     */
    public static final DatabaseHandle USER_DATA = handle.getSeparateCollection("user_data", "id");

    /**
     * Handle for Guild Data
     */
    public static final DatabaseHandle GUILD_DATA = handle.getSeparateCollection("bot", "id");

    /**
     * Private Constructor
     */
    private DatabaseManager()
    {
        cli = new MongoClient(new MongoClientURI(JsonHandler.handle.get("uri_link")));
        db = cli.getDatabase("Database");
        col = db.getCollection("bot");
    }

    /**
     * Gets a separate collection from the main.
     *
     * @param colName The requested collection
     * @param identifier The key to locate the document
     * @return The retrieved collection
     */
    public final DatabaseHandle getSeparateCollection(String colName, String identifier)
    {
        return new DatabaseHandle(db.getCollection(colName), identifier);
    }

    /**
     * @return The mongo client
     */
    public final MongoClient getClient()
    {
        return cli;
    }

    /**
     * @return The main mongo collection
     */
    public final MongoCollection<Document> getCollection()
    {
        return col;
    }

    /**
     * Utilized to access any collections
     *
     * @return The mongo database
     */
    public final MongoDatabase getDatabase()
    {
        return db;
    }
}
