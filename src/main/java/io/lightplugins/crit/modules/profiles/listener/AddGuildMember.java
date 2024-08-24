package io.lightplugins.crit.modules.profiles.listener;

import io.lightplugins.crit.modules.profiles.LightProfile;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class AddGuildMember extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {

        event.getGuild().getMembers().forEach(member -> {
            String uniqueId = member.getId();
            String username = member.getUser().getName();
            Instant instant = member.getTimeJoined().toInstant();
            long timeJoined = instant.toEpochMilli();
            LightPrinter.print("User " + username + " joined at " + timeJoined);
            boolean currentlyBanned = false;
            int coins = 0;
            long lastSeen = System.currentTimeMillis();

            boolean userExists = LightProfile.getLightProfileAPI().userExists(uniqueId);
            if (!userExists) {
                LightProfile.getLightProfileAPI().createNewProfile(uniqueId, username, currentlyBanned, coins, lastSeen, timeJoined);
                LightPrinter.print("User profile created for " + username);
            } else {
                LightPrinter.print("User profile already exists for " + username);
            }
        });
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        String uniqueId = event.getMember().getId();
        String username = event.getUser().getName();
        Instant instant = event.getMember().getTimeJoined().toInstant();
        long timeJoined = instant.toEpochMilli();
        LightPrinter.print("User " + username + " joined at " + timeJoined);
        boolean currentlyBanned = false;
        int coins = 0;
        long lastSeen = System.currentTimeMillis();

        boolean userExists = LightProfile.getLightProfileAPI().userExists(uniqueId);
        if (!userExists) {
            LightProfile.getLightProfileAPI().createNewProfile(uniqueId, username, currentlyBanned, coins, lastSeen, timeJoined);
            LightPrinter.print("New User profile created for " + username);
        } else {
            LightPrinter.print("New User profile already exists for " + username);
        }
    }
}