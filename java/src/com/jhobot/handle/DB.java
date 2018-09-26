package com.jhobot.handle;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import sx.blah.discord.handle.obj.IGuild;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DB
{
    private MongoClient cli;
    private MongoCollection col;
    private MongoDatabase db;

    public DB(String URI)
    {
        this.cli = new MongoClient(new MongoClientURI(URI));
        this.db = cli.getDatabase("Database");
        this.col = db.getCollection("bot");
    }

    public DB getSeperateCollection(String colName)
    {
        this.col = db.getCollection("bot");
        return this;
    }

    public MongoClient getClient()
    {
        return this.cli;
    }
    public MongoCollection getCollection()
    {
        return this.col;
    }
    public MongoDatabase getDatabase()
    {
        return this.db;
    }

    public boolean getBoolean(IGuild guild, String object)
    {
        Document get = (Document) col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return false;

        return (Boolean) get.get(object);
    }

    public String getString(IGuild guild, String object)
    {
        Document get = (Document) col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return null;

        return (String) get.get(object);
    }

    public void set(IGuild guild, String object, String entry)
    {
        Document get = (Document) col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return;

        col.updateOne(get, new Document("$set", new Document(object, entry)));
    }

    public void set(IGuild guild, String object, Boolean entry)
    {
        Document get = (Document) col.find(new Document("guildid", guild.getStringID())).first();

        if (get == null)
            return;

        col.updateOne(get, new Document("$set", new Document(object, entry)));
    }

    public boolean exists(IGuild guild)
    {
        Document get = (Document) col.find(new Document("guildid", guild.getStringID())).first();
        return get != null;
    }

    public String getStats(String object)
    {
        Document get = (Document) col.find(new Document("stats", true)).first();

        if (get == null)
            return null;

        return (String) get.get(object);
    }

    public void setStats(String object, String entry)
    {
        Document get = (Document) col.find(new Document("stats", true)).first();

        if (get == null)
            return;

        col.updateOne(get, new Document("$set", new Document(object, entry)));
    }
}
