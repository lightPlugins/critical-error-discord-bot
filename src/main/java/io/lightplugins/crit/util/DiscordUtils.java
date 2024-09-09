package io.lightplugins.crit.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.Ban;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DiscordUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static CompletableFuture<List<Ban>> getLastBans(Guild guild) {
        return guild.retrieveBanList().submit().thenApply(bans -> {
            List<Ban> lastBans = bans.stream()
                    .limit(5)
                    .collect(Collectors.toList());
            Collections.reverse(lastBans);
            return lastBans;
        });
    }

    public static String formatBanDate(Ban ban) {
        return ban.getUser().getTimeCreated().format(DATE_FORMATTER);
    }

    public static CompletableFuture<Integer> getBannedUserCount(Guild guild) {
        return guild.retrieveBanList().submit().thenApply(List::size);
    }
}