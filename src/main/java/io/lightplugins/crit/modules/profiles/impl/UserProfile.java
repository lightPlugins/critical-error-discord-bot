package io.lightplugins.crit.modules.profiles.impl;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import java.util.Date;
import java.util.HashMap;

@Setter
@Getter
public class UserProfile {

    private String uniqueId;
    private String username;
    private boolean currentlyBanned;
    private int coins;
    private long lastSeen;
    private long timeJoined;
    private Date birthday;
    // HashMap<ChannelId, Time>
    private HashMap<String, Double> activeTime = new HashMap<>();


}
