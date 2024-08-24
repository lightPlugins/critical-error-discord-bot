package io.lightplugins.crit.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class PermissionNode {

    public static boolean isAdmin(Member member) {

        List<Role> adminRoles = new ArrayList<>();

        // Admin Role
        adminRoles.add(member.getGuild().getRoleById("1195852555599745174"));
        // Owner Role
        adminRoles.add(member.getGuild().getRoleById("1081002997196267520"));
        // Leader Role
        adminRoles.add(member.getGuild().getRoleById("1261832512406294528"));
        // IT Role
        adminRoles.add(member.getGuild().getRoleById("1269399942737166426"));

        for(Role role : adminRoles) {
            if(member.getRoles().contains(role)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isMember(Member member) {
        Role memberRole = member.getGuild().getRoleById("1081003235109785710");
        return member.getRoles().contains(memberRole);
    }

    public static boolean isGuest(Member member) {
        Role guestRole = member.getGuild().getRoleById("1140689400968253471");
        return member.getRoles().contains(guestRole);
    }

    public static boolean isFriend(Member member) {
        Role friendRole = member.getGuild().getRoleById("1085286861280387072");
        return member.getRoles().contains(friendRole);
    }

    public static boolean isSupporter(Member member) {
        Role supporterRole = member.getGuild().getRoleById("1269467265909260360");
        return member.getRoles().contains(supporterRole);
    }
}
