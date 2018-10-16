package com.jhobot.handle;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;

public class DB
{
    private final MongoClient cli;
    private MongoCollection<Document> col;
    private final MongoDatabase db;

    public DB(String URI)
    {
        this.cli = new MongoClient(new MongoClientURI(URI));
        this.db = cli.getDatabase("Database");
        this.col = db.getCollection("bot");
    }

    public DB getSeperateCollection(String colName)
    {
        this.col = db.getCollection(colName);
        return this;
    }

    public MongoClient getClient()
    {
        return this.cli;
    }
    public MongoCollection<Document> getCollection()
    {
        return this.col;
    }
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

    public void set(IGuild guild, String object, String entry)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return;

        col.updateOne(get, new Document("$set", new Document(object, entry)));
    }

    public void set(IGuild guild, String object, Boolean entry)
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

    public String getStats(String object)
    {
        Document get = col.find(new Document("stats", true)).first();

        if (get == null)
            return null;

        return (String) get.get(object);
    }

    public void setStats(String object, String entry)
    {
        Document get = col.find(new Document("stats", true)).first();

        if (get == null)
            return;

        col.updateOne(get, new Document("$set", new Document(object, entry)));
    }
}
