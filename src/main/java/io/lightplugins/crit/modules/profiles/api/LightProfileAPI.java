package io.lightplugins.crit.modules.profiles.api;

import io.lightplugins.crit.modules.profiles.impl.UserProfile;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.database.SQLDatabase;
import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.util.database.model.TableNames;

import java.sql.Date;
import java.util.concurrent.ExecutionException;

public class LightProfileAPI {

    public void createNewProfile(String uniqueId, String username, String ipAddress, int coins, java.util.Date lastSeen) {

        UserProfile userProfile = new UserProfile();
        userProfile.setUniqueId(uniqueId);
        userProfile.setUsername(username);
        userProfile.setIpAddress(ipAddress);
        userProfile.setCoins(coins);
        userProfile.setLastSeen(lastSeen);
        insertProfile(userProfile);
    }

    public void insertProfile(UserProfile userProfile) {
        SQLDatabase database = LightMaster.instance.getDatabase();
        String sql = "INSERT INTO " + TableNames.USER_DATA.getTableName() + " (uniqueId, username, ipAddress, coins, lastSeen) VALUES (?, ?, ?, ?, ?)";
        database.insertIntoDatabaseAsync(sql,
                        userProfile.getUniqueId(),
                        userProfile.getUsername(),
                        userProfile.getIpAddress(),
                        userProfile.getCoins(),
                        userProfile.getLastSeen())
                .thenAccept(success -> {
                    if (success) {
                        LightPrinter.print("User profile inserted successfully.");
                    } else {
                        throw new RuntimeException("Failed to insert user profile.");
                    }
                });

        if(database.insertIntoDatabase(sql,
                userProfile.getUniqueId(),
                userProfile.getUsername(),
                userProfile.getIpAddress(),
                userProfile.getCoins(),
                new Date(userProfile.getLastSeen().getTime()))) {

            LightPrinter.print("User profile inserted successfully.");
        } else {
            throw new RuntimeException("Failed to insert user profile.");
        }
    }

    public boolean userExists(String uniqueId) {
        SQLDatabase database = LightMaster.instance.getDatabase();
        String sql = "SELECT COUNT(*) FROM " + TableNames.USER_DATA.getTableName() + " WHERE uniqueId = ?";
        try {
            Integer count = database.queryDatabaseAsync(sql, Integer.class, uniqueId).get();
            return count != null && count > 0;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to check if user exists.", e);
        }
    }
}

