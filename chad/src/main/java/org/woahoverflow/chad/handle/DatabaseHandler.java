package org.woahoverflow.chad.handle;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;

public class DatabaseHandler
{
    private final MongoClient cli;
    private MongoCollection<Document> col;
    private final MongoDatabase db;

    public DatabaseHandler(String uri)
    {
        cli = new MongoClient(new MongoClientURI(uri));
        db = cli.getDatabase("Database");
        col = db.getCollection("bot");
    }

    @SuppressWarnings("unused")
    public final DatabaseHandler getSeperateCollection(String colName)
    {
        col = db.getCollection(colName);
        return this;
    }

    @SuppressWarnings("unused")
    public final MongoClient getClient()
    {
        return cli;
    }
    public final MongoCollection<Document> getCollection()
    {
        return col;
    }
    @SuppressWarnings("unused")
    public final MongoDatabase getDatabase()
    {
        return db;
    }


    @SuppressWarnings("unchecked")
    public final ArrayList<String> getArray(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null) {
            return null;
        }

        ArrayList<String> ar = (ArrayList<String>) get.get(object);
        if (ar == null) {
            return null;
        }
        return ar;
    }
    public final boolean getBoolean(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null) {
            return false;
        }

        return (Boolean) get.get(object);
    }

    public final String getString(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null) {
            return null;
        }

        return (String) get.get(object);
    }

    public final Object get(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null) {
            return null;
        }

        return get.get(object);
    }

    public final boolean contains(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null) {
            return false;
        }

        return get.containsKey(object);
    }

    public final void set(IGuild guild, String object, Object entry)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null) {
            return;
        }

        col.updateOne(get, new Document("$set", new Document(object, entry)));
    }

    public final boolean exists(IGuild guild)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();
        return get != null;
    }
}
