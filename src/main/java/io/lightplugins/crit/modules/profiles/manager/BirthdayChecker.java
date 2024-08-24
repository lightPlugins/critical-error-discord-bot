package io.lightplugins.crit.modules.profiles.manager;

import io.lightplugins.crit.modules.profiles.api.LightProfileAPI;
import io.lightplugins.crit.modules.profiles.impl.UserProfile;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BirthdayChecker {

    private final LightProfileAPI profileAPI;
    private final JDA jda;
    private final String channelId;
    private final ScheduledExecutorService scheduler;

    public BirthdayChecker(LightProfileAPI profileAPI, JDA jda, String channelId) {
        this.profileAPI = profileAPI;
        this.jda = jda;
        this.channelId = channelId;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduleDailyCheck();
        scheduleTestCheck();
        LightPrinter.print("BirthdayChecker has been enabled.");
    }

    private void scheduleDailyCheck() {
        try {
            long initialDelay = calculateInitialDelay(LocalTime.of(11, 0));
            long period = TimeUnit.DAYS.toMillis(1);

            scheduler.scheduleAtFixedRate(this::checkBirthdays, initialDelay, period, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LightPrinter.printError("Failed to schedule birthday check: " + e.getMessage());
        }
    }

    public void scheduleTestCheck() {
        LightPrinter.print("Scheduling test birthday check...");
        try {
            LocalTime testTime = LocalTime.now().plusSeconds(4); // Set target time to 4 seconds from now
            long initialDelay = calculateInitialDelay(testTime);
            long period = TimeUnit.DAYS.toMillis(1);

            LightPrinter.printWatchdog("Initial delay: " + initialDelay + "ms");

            scheduler.scheduleAtFixedRate(this::checkBirthdays, initialDelay, period, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LightPrinter.printError("Failed to schedule test birthday check: " + e.getMessage());
        }
    }

    private long calculateInitialDelay(LocalTime targetTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.with(targetTime);
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }
        return Duration.between(now, nextRun).toMillis();
    }

    private void checkBirthdays() {
        LocalDate today = LocalDate.now();
        List<UserProfile> allUsers = profileAPI.getUserProfiles();
        LightPrinter.print("Checking birthdays from " + allUsers.size() + " users.");

        boolean birthdayToday = false;

        for(UserProfile user : allUsers) {
            LightPrinter.printDebug("Checking user: " + user.getBirthday());

            if(user.getBirthday() == null) {
                LightPrinter.printDebug("User " + user.getUsername() + " has no birthday set.");
                continue;
            }
            Date birthday = user.getBirthday();
            LocalDate birthdayDate = Instant.ofEpochMilli(birthday.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LightPrinter.printDebug("Month: " + birthdayDate.getMonth() + ", Day: " + birthdayDate.getDayOfMonth());
            LightPrinter.printDebug("Today Month: " + today.getMonth() + ", Today Day: " + today.getDayOfMonth());

            if(birthdayDate.getMonth() == today.getMonth() && birthdayDate.getDayOfMonth() == today.getDayOfMonth()) {
                LightPrinter.print("It's " + user.getUsername() + "'s birthday! He is now " + (today.getYear() - birthdayDate.getYear()) + " years old.");
                birthdayToday = true;
            }
        }

        if(!birthdayToday) {
            LightPrinter.print("No birthdays today.");
        }
    }

    private void postMessage(String message) {
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            channel.sendMessage(message).queue();
        } else {
            LightPrinter.printError("Channel not found: " + channelId);
        }
    }

    // Call this method to properly shut down the scheduler when the application is closing
    public void shutdownScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            LightPrinter.printWatchdog("BirthdayChecker scheduler has been shut down.");
        }
    }
}