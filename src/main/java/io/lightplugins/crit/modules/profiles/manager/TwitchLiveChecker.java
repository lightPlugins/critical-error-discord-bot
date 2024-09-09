package io.lightplugins.crit.modules.profiles.manager;

import io.lightplugins.crit.modules.profiles.LightProfile;
import io.lightplugins.crit.modules.profiles.lookup.TwitchAPI;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TwitchLiveChecker {

    private final List<String> twitchChannels;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final TwitchAPI twitchAPI;
    private final NewsChannel notificationChannel;

    public TwitchLiveChecker(NewsChannel notificationChannel, TwitchAPI twitchAPI) {
        this.twitchChannels = LightProfile.instance.getTwitchConfig().getStringList("user-to-track");
        this.notificationChannel = notificationChannel;
        this.twitchAPI = twitchAPI;

        if(twitchAPI == null) {
            LightPrinter.printError("TwitchAPI is null, cannot check live status. Aborting...");
            return;
        }
        startScheduler();
        LightPrinter.print("TwitchLiveChecker has been enabled.");
    }

    private void startScheduler() {
        scheduler.scheduleAtFixedRate(this::checkLiveStatus, 2, 2, TimeUnit.MINUTES);
    }

    private void checkLiveStatus() {
        LightPrinter.print("Checking Twitch live status now ...");
        boolean isSomeoneLive = false;
        for (String channelName : twitchChannels) {
            if (twitchAPI.isUserLive(channelName)) {
                if(LightProfile.instance.getStreamAlreadyPostet().contains(channelName)) {
                    LightPrinter.print("User " + channelName + " is already live, skipping notification.");
                    continue;
                }
                LightPrinter.print("User " + channelName + " is live!");
                isSomeoneLive = true;
                sendLiveNotification(channelName, twitchAPI.getProfileImageUrl(), twitchAPI.getStreamTitle());
                LightPrinter.print("Notification sent.");
                LightProfile.instance.getStreamAlreadyPostet().add(channelName);
            }
        }

        if(!isSomeoneLive) {
            LightPrinter.print("No one is live on Twitch right now.");
        }
    }

    private void sendLiveNotification(String channelName, String profileImageUrl, String streamTitle) {
        profileImageUrl = profileImageUrl.replace("{width}", "300").replace("{height}", "300");
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(channelName + " ist jetzt LIVE auf TWITCH!");
        embedBuilder.setDescription("Schau bei ihm vorbei: https://www.twitch.tv/" + channelName);
        embedBuilder.setThumbnail(profileImageUrl);
        embedBuilder.addField("Aktueller Stream:", streamTitle, false);
        embedBuilder.setColor(HexFormat.fromHexDigits("6441A5"));
        notificationChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}