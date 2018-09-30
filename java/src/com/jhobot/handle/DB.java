package com.jhobot.handle;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonArray;
import org.bson.Document;
import org.bson.conversions.Bson;
import sx.blah.discord.handle.obj.IGuild;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        this.col = db.getCollection(colName);
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

    public ArrayList<String> getPicArray()
    {
        getSeperateCollection("catgallery");
        Document get = (Document) col.find(new Document("use", "gal")).first();

        return (ArrayList<String>) get.get("pic");
    }

    public File getRandomCatPicture()
    {
        getSeperateCollection("catgallery");
        Document get = (Document) col.find(new Document("use", "gal")).first();

        int i = new Random().nextInt(Integer.parseInt((String) get.get("amount")));

        return new File(System.getenv("appdata") + "\\jho\\catpictures\\" + getPicArray().get(i));
    }

    public void addPictureToArray(String file)
    {
        getSeperateCollection("catgallery");
        Document get = (Document) col.find(new Document("use", "gal")).first();

        List<String> oof = new ArrayList<>(getPicArray());
        oof.add(file);
        col.updateOne(get, new Document("$set", new Document("pic", oof)));
        updateAmount();
    }

    public void updateAmount()
    {
        getSeperateCollection("catgallery");
        Document get = (Document) col.find(new Document("use", "gal")).first();

        col.updateOne(get, new Document("$set", new Document("amount", Integer.toString(Integer.parseInt((String) get.get("amount")) + 1))));
    }
}
