package io.lightplugins.crit.modules.profiles;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.modules.profiles.api.LightProfileAPI;
import io.lightplugins.crit.modules.profiles.commands.AddBirthdayCommand;
import io.lightplugins.crit.modules.profiles.impl.UserProfile;
import io.lightplugins.crit.modules.profiles.listener.AddGuildMember;
import io.lightplugins.crit.modules.profiles.listener.CheckActivity;
import io.lightplugins.crit.modules.profiles.listener.SaveMessages;
import io.lightplugins.crit.modules.profiles.manager.BirthdayChecker;
import io.lightplugins.crit.modules.watchdog.commands.ActiveTimeCommand;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.database.model.TableNames;
import io.lightplugins.crit.util.interfaces.LightModule;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
     * Author: lightPlugins
     * Project: CriticalError Discord Bot
     * Date: 2023-10-05
     */

public class LightProfile implements LightModule {

    @Getter
    private static LightProfileAPI lightProfileAPI;
    private final CheckActivity checkActivity = new CheckActivity();

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void enable() {
        lightProfileAPI = new LightProfileAPI();
        // lightProfileAPI.addCurrentlyBannedColumn(); // one time setup !!! adds new column to the table

        String channelId = "YOUR_CHANNEL_ID";

        LightPrinter.print("LightProfile has been enabled.");

        LightMaster.instance.getDatabase()
                .createTable(TableNames.USER_DATA.getTableName(),
                        "uniqueId VARCHAR(36) PRIMARY KEY, " +
                                "username VARCHAR(128), " +
                                "currentlyBanned INT, " +
                                "lastSeen DATE, " +
                                "timeJoined VARCHAR(100), " +
                                "coins INT, " +
                                "birthday VARCHAR(100)"
                );

        LightMaster.instance.getDatabase()
                .createTable(TableNames.CHANNEL_TIME.getTableName(),
                        "uniqueId VARCHAR(36), " +
                                "channelId VARCHAR(36), " +
                                "activeTime DOUBLE, " +
                                "PRIMARY KEY (uniqueId, channelId)"
                );

        // Erstellen der Tabelle für Chatnachrichten
        LightMaster.instance.getDatabase()
                .createTable(TableNames.CHAT_MESSAGES.getTableName(),
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                                "messageID VARCHAR(36), " +
                                "userID VARCHAR(36), " +
                                "messageText TEXT, " +
                                "timestamp TIMESTAMP"
                );

        // Erstellen der Tabelle für Nachrichten anhänge
        LightMaster.instance.getDatabase()
                .createTable(TableNames.CHAT_ATTACHMENTS.getTableName(),
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                                "attachmentID VARCHAR(36), " +
                                "messageID VARCHAR(36), " +
                                "userID VARCHAR(36), " +
                                "filePath TEXT, " +
                                "mediaType VARCHAR(50), " +
                                "FOREIGN KEY (messageID) REFERENCES " + TableNames.CHAT_MESSAGES.getTableName() + "(messageID)"
                );

        lightProfileAPI.syncUserProfilesToRAM();

        LightMaster.instance.getShardManager().addEventListener(
                new AddGuildMember(),
                new AddBirthdayCommand(),
                new CheckActivity(),
                new ActiveTimeCommand(),
                new SaveMessages()
        );

        scheduleSyncToDatabase();
        BirthdayChecker birthdayChecker = new BirthdayChecker(lightProfileAPI, LightMaster.instance.getShardManager().getShardById(0), channelId);
    }

    @Override
    public void disable() {
        String id = getCurrentGuildId();
        LightPrinter.printDebug("Guild ID: " + id);
        checkActivity.saveActiveTimeForAllUsers(LightMaster.instance.getShardManager().getGuildById(id));
        syncToDatabase();
    }

    @Override
    public void reload() {
        syncToDatabase();
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public String getName() {
        return "LightProfile";
    }

    private void scheduleSyncToDatabase() {
        scheduler.scheduleAtFixedRate(this::syncToDatabase, 2, 10, TimeUnit.MINUTES);
    }

    private void syncToDatabase() {
        long start = System.currentTimeMillis();
        LightPrinter.print("Syncing user profiles to database ...");
        String sql = "INSERT OR REPLACE INTO " + TableNames.CHANNEL_TIME.getTableName() + " (uniqueId, channelId, activeTime) VALUES (?, ?, ?)";
        try (Connection connection = LightMaster.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (UserProfile userProfile : lightProfileAPI.getUserProfiles()) {

                userProfile.getActiveTime().forEach((channelId, time) -> {
                    try {
                        statement.setString(1, userProfile.getUniqueId());
                        statement.setString(2, channelId);
                        statement.setDouble(3, time);
                        statement.addBatch();
                    } catch (SQLException e) {
                        LightPrinter.printError("Could not sync user profiles to database.");
                        e.printStackTrace();
                    }
                });
            }
            statement.executeBatch();
            long end = System.currentTimeMillis();
            LightPrinter.print("Synced user profiles to database in " + (end - start) + "ms.");
        } catch (SQLException e) {
            LightPrinter.printError("Could not sync user profiles to database.");
            e.printStackTrace();
        }
    }

    public String getCurrentGuildId() {
        Guild guild = LightMaster.instance.getShardManager().getGuilds().stream().findFirst().orElse(null);
        if (guild != null) {
            return guild.getId();
        } else {
            LightPrinter.printError("No guilds found.");
            return null;
        }
    }
}
