// CheckActivity.java
package io.lightplugins.crit.modules.profiles.listener;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.modules.profiles.LightProfile;
import io.lightplugins.crit.modules.profiles.impl.UserProfile;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class CheckActivity extends ListenerAdapter {


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
            Long joinTime = LightProfile.getLightProfileAPI().getJoinTimes().remove(userId);
            if (joinTime != null) {

                if(event.getChannelJoined() != null) {
                    if(Objects.equals(event.getChannelJoined().getParentCategoryId(), "1275200581736206346")) {
                        LightPrinter.printWatchdog("User joined a channel in the blacklist category");
                        return;
                    }

                    if(Objects.equals(event.getChannelJoined().getParentCategoryId(), "1275200365033295872")) {
                        LightPrinter.printWatchdog("User joined a channel in the blacklist category");
                        return;
                    }

                    if(event.getChannelJoined().equals(afkChannel)) {
                        LightPrinter.printWatchdog("User " + event.getMember().getUser().getName() + " joined the AFK channel");
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
            LightProfile.getLightProfileAPI().getJoinTimes().put(userId, System.currentTimeMillis());
        }
    }

    // Method to calculate and save active time for all users in voice channels
    public void saveActiveTimeForAllUsers(Guild guild) {

        long currentTime = System.currentTimeMillis();
        LightPrinter.printDebug("List size: " + LightProfile.getLightProfileAPI().getJoinTimes().size());
        for (Map.Entry<String, Long> entry : LightProfile.getLightProfileAPI().getJoinTimes().entrySet()) {
            LightPrinter.print("Saving active time for user " + entry.getKey() + " while shutdown");
            String userId = entry.getKey();
            Long joinTime = entry.getValue();
            double timeSpent = (currentTime - joinTime) / 1000.0;
            UserProfile userProfile = LightProfile.getLightProfileAPI().getUserProfile(userId);
            if (userProfile != null) {
                // Fetch the current channel ID using JDA

                Member member = guild.getMemberById(userId);

                if(member == null) {
                    LightPrinter.printError("Could not get user " + userId);
                    return;
                }

                if(member.getVoiceState() == null) {
                    LightPrinter.printError("Could not get voice state for user " + userId);
                    return;
                }

                AudioChannelUnion currentChannel = member.getVoiceState().getChannel();
                if (currentChannel != null) {
                    String currentChannelId = currentChannel.getId();
                    userProfile.getActiveTime().merge(currentChannelId, timeSpent, Double::sum);
                    LightPrinter.print("Saved active time for user " + member.getUser().getName() + " in channel " + currentChannel.getName() + " while shutdown");
                } else {
                    LightPrinter.print("User " + member.getUser().getName() + " is not in a voice channel while shutdown");
                }

            }
        }
    }

    // Method to resume time tracking for all users when the guild is ready / restarted / reconnected
    @Override
    public void onGuildReady(GuildReadyEvent event) {

        LightPrinter.print("Guild is now available, resuming time tracking for all users.");

        // Continue with the rest of the logic
        for (Member member : event.getGuild().getMembers()) {
            if (member.getVoiceState() != null && member.getVoiceState().getChannel() != null) {
                if(LightProfile.getLightProfileAPI().getJoinTimes().get(member.getId()) == null) {
                    LightPrinter.printDebug("Resuming time tracking for user " + member.getUser().getName());
                    LightProfile.getLightProfileAPI().getJoinTimes().put(member.getId(), System.currentTimeMillis());
                } else {
                    // semi check if the guild is not available / connection issues
                    LightPrinter.printWatchdog("Detected connection issues. DiscordAPI was not available.");
                    LightPrinter.printWatchdog("Bot is now reconnected to DiscordAPI.");
                }
            }
        }
    }
}