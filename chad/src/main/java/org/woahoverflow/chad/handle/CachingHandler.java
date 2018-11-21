package org.woahoverflow.chad.handle;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.ui.UIHandler;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;

public class CachingHandler
{
    public class CachedGuild
    {
        private final Document doc;
        private final IGuild guild;
        private final String cacheTime;
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
    private final IDiscordClient cli;
    private final MongoCollection<Document> col = ChadVar.DATABASE_DEVICE.getCollection();
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
        ChadVar.UI_DEVICE.addLog("Caching guild '"+guild.getStringID()+"'.", UIHandler.LogLevel.CACHING);
        ChadVar.GUILD_CACHE.put(guild, new CachedGuild(guild, get));
    }

    public void unCacheGuild(IGuild guild)
    {
        ChadVar.UI_DEVICE.addLog("UnCached guild '"+guild.getStringID()+"'.", UIHandler.LogLevel.CACHING);
        ChadVar.GUILD_CACHE.remove(guild);
    }

    private void cacheAll()
    {
        ChadVar.UI_DEVICE.addLog("ReCaching all guilds.", UIHandler.LogLevel.CACHING);
        ChadVar.LAST_CACHE_ALL = Util.getTimeStamp();
        ChadVar.UI_DEVICE.update();
        cli.getGuilds().forEach(this::cacheGuild);
    }

    private void unCacheAll()
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
