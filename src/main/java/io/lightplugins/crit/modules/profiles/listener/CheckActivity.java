// CheckActivity.java
package io.lightplugins.crit.modules.profiles.listener;

import io.lightplugins.crit.modules.profiles.LightProfile;
import io.lightplugins.crit.modules.profiles.impl.UserProfile;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CheckActivity extends ListenerAdapter {

    private final Map<String, Long> joinTimes = new HashMap<>();

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        String userId = event.getMember().getId();
        String channelIdLeft = event.getChannelLeft() != null ? event.getChannelLeft().getId() : null;
        String channelIdJoined = event.getChannelJoined() != null ? event.getChannelJoined().getId() : null;

        VoiceChannel afkChannel = event.getGuild().getAfkChannel();

        if(afkChannel == null) {
            LightPrinter.printError("AFK Channel not found");
        }

        if (channelIdLeft != null) {

            if(Objects.equals(event.getChannelLeft().getParentCategoryId(), "1275200581736206346")) {
                LightPrinter.printWatchdog("User joined a channel in the Watchdog category");
                return;
            }

            if(Objects.equals(event.getChannelLeft().getParentCategoryId(), "1275200365033295872")) {
                LightPrinter.printWatchdog("User joined a channel in the Watchdog category");
                return;
            }

            // User left a voice channel
            Long joinTime = joinTimes.remove(userId);
            if (joinTime != null) {

                if(event.getChannelJoined() != null) {
                    if(Objects.equals(event.getChannelJoined().getParentCategoryId(), "1275200581736206346")) {
                        LightPrinter.printWatchdog("User joined a channel in the Watchdog category");
                        return;
                    }

                    if(Objects.equals(event.getChannelJoined().getParentCategoryId(), "1275200365033295872")) {
                        LightPrinter.printWatchdog("User joined a channel in the Watchdog category");
                        return;
                    }
                }

                double timeSpent = (System.currentTimeMillis() - joinTime) / 1000.0;
                UserProfile userProfile = LightProfile.getLightProfileAPI().getUserProfile(userId);
                if (userProfile != null) {
                    userProfile.getActiveTime().merge(channelIdLeft, timeSpent, Double::sum);
                }
            }
        }

        if (channelIdJoined != null) {
            // User joined a new voice channel
            joinTimes.put(userId, System.currentTimeMillis());
        }
    }
}