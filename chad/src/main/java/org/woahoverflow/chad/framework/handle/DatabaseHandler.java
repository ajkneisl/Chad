package org.woahoverflow.chad.framework.handle;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;

/**
 * Accesses the database
 *
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class DatabaseHandler
{

    /**
     * The global handle for the Database
     */
    public static final DatabaseHandler handle = new DatabaseHandler();

    // Local Variables
    private final MongoClient cli;
    private MongoCollection<Document> col;
    private final MongoDatabase db;

    /**
     * Private Constructor
     */
    private DatabaseHandler()
    {
        cli = new MongoClient(new MongoClientURI(JsonHandler.handle.get("uri_link")));
        db = cli.getDatabase("Database");
        col = db.getCollection("bot");
    }

    /**
     * Gets a separate collection from the main.
     *
     * @param colName The requested collection
     * @return The retrieved collection
     */
    public final DatabaseHandler getSeparateCollection(String colName)
    {
        col = db.getCollection(colName);
        return this;
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


    @SuppressWarnings("all")
    /**
     * Gets an array from the guild's document
     *
     * @param guild The guild to retrieve from
     * @param object The array's name
     * @return The retrieved array
     */
    public final synchronized ArrayList<String> getArray(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return null;

        ArrayList<String> ar = (ArrayList<String>) get.get(object);
        if (ar == null)
            return null;
        return ar;
    }

    /**
     * Gets a String from the guild's document
     *
     * @param guild The guild to retrieve from
     * @param object The string's name
     * @return The retrieved string
     */
    public final synchronized String getString(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return null;

        return (String) get.get(object);
    }

    /**
     * Gets an object from the guild's document
     *
     * @param guild The guild to retrieve from
     * @param object The object's name
     * @return The retrieved object
     */
    public final synchronized Object get(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return null;

        return get.get(object);
    }

    /**
     * Checks to see if the guild's document contains an object
     *
     * @param guild The guild to check at
     * @param object The object to check
     * @return If the object exists
     */
    public final synchronized boolean contains(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return false;

        return get.containsKey(object);
    }

    /**
     * Gets a boolean from the guild's document
     *
     * @param guild The guild to retrieve from
     * @param object The boolean's name
     * @return The retrieved object
     */
    public final synchronized boolean getBoolean(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return false;

        return (Boolean) get.get(object);
    }

    /**
     * Sets a value in the guild's document
     *
     * @param guild The guild to set at
     * @param object The object to set
     * @param entry The new value for the object
     */
    public final synchronized void set(IGuild guild, String object, Object entry)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return;

        col.updateOne(get, new Document("$set", new Document(object, entry)));
    }

    /**
     * Checks to see if a guild's document exist
     *
     * @param guild The guild to check
     * @return If the document exists
     */
    public final synchronized boolean exists(IGuild guild)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();
        return get != null;
    }
}
