package org.woahoverflow.chad.handle;

import org.woahoverflow.chad.handle.logging.LogLevel;
import org.woahoverflow.chad.core.ChadVar;
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
        ChadVar.UI_HANDLER.addLog("Caching guild '"+guild.getStringID()+"'.", LogLevel.CACHING);
        ChadVar.GUILD_CACHE.put(guild, new CachedGuild(guild, get));
    }

    public void unCacheGuild(IGuild guild)
    {
        ChadVar.UI_HANDLER.addLog("UnCached guild '"+guild.getStringID()+"'.", LogLevel.CACHING);
        ChadVar.GUILD_CACHE.remove(guild);
    }

    public void cacheAll()
    {
        ChadVar.UI_HANDLER.addLog("ReCaching all guilds.", LogLevel.CACHING);
        ChadVar.LAST_CACHE_ALL = Util.getTimeStamp();
        ChadVar.UI_HANDLER.update();
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
