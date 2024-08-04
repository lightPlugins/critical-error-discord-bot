package io.lightplugins.crit.util.models;

import io.github.cdimascio.dotenv.Dotenv;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordShardBuilder {

    public DefaultShardManagerBuilder initShardManager(Dotenv envConfig) {

        LightPrinter.print("Enable dotenv ...");

        LightPrinter.print("Building shard manager ...");
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(envConfig.get("TOKEN"));

        LightPrinter.print("Set bot status to online");
        builder.setStatus(OnlineStatus.valueOf(envConfig.get("BOT_STATUS")));

        LightPrinter.print("Set bot activity to 'Playing "
                + envConfig.get("BOT_ACTIVITY_NAME").toLowerCase() + "'");
        builder.setActivity(Activity.playing(envConfig.get("BOT_ACTIVITY_NAME")));

        builder.setMemberCachePolicy(MemberCachePolicy.ALL);

        LightPrinter.print("Enable intents ...");
        builder.enableIntents(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_VOICE_STATES
        );

        LightPrinter.print("Enable cache policy ...");
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);

        LightPrinter.print("Enable chunky filter ...");
        builder.setChunkingFilter(ChunkingFilter.ALL);

        LightPrinter.print("Enable cache flags ...");
        builder.enableCache(
                CacheFlag.ONLINE_STATUS,
                CacheFlag.ROLE_TAGS,
                CacheFlag.EMOJI,
                CacheFlag.ACTIVITY,
                CacheFlag.VOICE_STATE
        );

        return builder;
    }
}
