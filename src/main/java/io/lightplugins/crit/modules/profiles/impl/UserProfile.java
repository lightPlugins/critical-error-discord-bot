package io.lightplugins.crit.modules.profiles.impl;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class UserProfile {

    private String uniqueId;
    private String username;
    private String ipAddress;
    private int coins;
    private Date lastSeen;


}
