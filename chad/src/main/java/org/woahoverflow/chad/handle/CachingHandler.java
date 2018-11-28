package org.woahoverflow.chad.handle;

import com.mongodb.client.MongoCollection;
import java.util.concurrent.ConcurrentHashMap;
import org.bson.Document;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.ui.UIHandler;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;

public class CachingHandler
{
    private static final ConcurrentHashMap<IGuild, CachedGuild> GUILD_CACHE = new ConcurrentHashMap<>();

    public static class CachedGuild
    {
        private final Document doc;
        private final IGuild guild;
        private final String cacheTime;
        private CachedGuild(IGuild guild, Document doc)
        {
            this.doc = doc;
            this.guild = guild;
            cacheTime = Util.getTimeStamp();
        }
        public final String lastCached()
        {
            return cacheTime;
        }
        public final IGuild getGuild()
        {
            return guild;
        }
        public final Document getDoc()
        {
            return doc;
        }
    }
    private final IDiscordClient cli;
    private final MongoCollection<Document> col = ChadVar.databaseDevice.getCollection();
    public CachingHandler(IDiscordClient cli)
    {
        this.cli = cli;
    }

    public final void cacheGuild(IGuild guild)
    {
        Document get = col.find(new Document("guildid", guild.getStringID())).first();
        if (get == null)
        {
            return;
        }
        ChadVar.uiDevice.addLog("Caching guild '"+guild.getStringID()+"'.", UIHandler.LogLevel.CACHING);
        GUILD_CACHE.put(guild, new CachedGuild(guild, get));
    }

    public static void unCacheGuild(IGuild guild)
    {
        ChadVar.uiDevice.addLog("UnCached guild '"+guild.getStringID()+"'.", UIHandler.LogLevel.CACHING);
        GUILD_CACHE.remove(guild);
    }

    private void cacheAll()
    {
        ChadVar.uiDevice.addLog("ReCaching all guilds.", UIHandler.LogLevel.CACHING);
        ChadVar.lastCacheAll = Util.getTimeStamp();
        ChadVar.uiDevice.update();
        cli.getGuilds().forEach(this::cacheGuild);
    }

    private static void unCacheAll()
    {
        GUILD_CACHE.clear();
    }

    public final void reCacheAll()
    {
        unCacheAll();
        cacheAll();
    }

    public static int cachedGuildsSize()
    {
        return GUILD_CACHE.size();
    }

    public static CachedGuild getGuild(IGuild guild)
    {
        return GUILD_CACHE.get(guild);
    }
}
