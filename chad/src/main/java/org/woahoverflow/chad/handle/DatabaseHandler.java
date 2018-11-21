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

    public DatabaseHandler(String URI)
    {
        this.cli = new MongoClient(new MongoClientURI(URI));
        this.db = cli.getDatabase("Database");
        this.col = db.getCollection("bot");
    }

    @SuppressWarnings("unused")
    public DatabaseHandler getSeperateCollection(String colName)
    {
        this.col = db.getCollection(colName);
        return this;
    }

    @SuppressWarnings("unused")
    public MongoClient getClient()
    {
        return this.cli;
    }
    public MongoCollection<Document> getCollection()
    {
        return this.col;
    }
    @SuppressWarnings("unused")
    public MongoDatabase getDatabase()
    {
        return this.db;
    }


    @SuppressWarnings("unchecked")
    public ArrayList<String> getArray(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null) return null;

        ArrayList<String> ar = (ArrayList<String>) get.get(object);
        if (ar == null)
            return null;
        return ar;
    }
    public boolean getBoolean(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return false;

        return (Boolean) get.get(object);
    }

    public String getString(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return null;

        return (String) get.get(object);
    }

    public Object get(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return null;

        return get.get(object);
    }

    public boolean contains(IGuild guild, String object)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return false;

        return get.containsKey(object);
    }

    public void set(IGuild guild, String object, Object entry)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return;

        col.updateOne(get, new Document("$set", new Document(object, entry)));
    }

    public boolean exists(IGuild guild)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();
        return get != null;
    }
}
