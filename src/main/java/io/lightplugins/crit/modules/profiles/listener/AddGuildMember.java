package io.lightplugins.crit.modules.profiles.listener;

import io.lightplugins.crit.modules.profiles.LightProfile;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Date;
import java.util.UUID;

public class AddGuildMember extends ListenerAdapter {

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getGuild().getMembers().forEach(member -> {
            String uniqueId = member.getId();
            String username = member.getUser().getName();
            String ipAddress = "0.0.0.0"; // Placeholder, as IP address is not available from JDA
            int coins = 0;
            Date lastSeen = new Date();

            boolean userExists = LightProfile.getLightProfileAPI().userExists(uniqueId);
            if (!userExists) {
                LightProfile.getLightProfileAPI().createNewProfile(uniqueId, username, ipAddress, coins, lastSeen);
                LightPrinter.print("User profile created for " + username);
            } else {
                LightPrinter.print("User profile already exists for " + username);
            }
        });
    }
}