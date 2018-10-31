package com.jhobot.handle;

import com.jhobot.core.ChadVar;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;

public class CachingHandler
{
    public class CachedGuild
    {
        private Document doc;
        private IGuild guild;
        private String cacheTime;
        private CachedGuild(IGuild guild, Document doc)
        {
            this.doc = doc;
            this.guild = guild;
            this.cacheTime = Util.getTimeStamp();
        }
        public String lastCached()
        {
            return this.cacheTime;
        }
        public IGuild getGuild()
        {
            return this.guild;
        }
        public Document getDoc()
        {
            return this.doc;
        }
    }
    private IDiscordClient cli;
    private MongoCollection<Document> col = ChadVar.DATABASE_HANDLER.getCollection();
    public CachingHandler(IDiscordClient cli)
    {
        this.cli = cli;
    }

    public void cacheGuild(IGuild guild)
    {
        Document get = this.col.find(new Document("guildid", guild.getStringID())).first();
        if (get == null)
        {
            return;
        }
        ChadVar.GUILD_CACHE.put(guild, new CachedGuild(guild, get));
    }

    public void unCacheGuild(IGuild guild)
    {
        ChadVar.GUILD_CACHE.remove(guild);
    }

    public void cacheAll()
    {
        cli.getGuilds().forEach(this::cacheGuild);
    }

    public void unCacheAll()
    {
        ChadVar.GUILD_CACHE.clear();
    }

    public void reCacheAll()
    {
        unCacheAll();
        cacheAll();
    }

    public CachedGuild getGuild(IGuild guild)
    {
        return ChadVar.GUILD_CACHE.get(guild);
    }
}
