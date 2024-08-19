package io.lightplugins.crit.modules.profiles.api;

import io.lightplugins.crit.modules.profiles.impl.UserProfile;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.database.SQLDatabase;
import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.util.database.model.TableNames;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LightProfileAPI {

    public void createNewProfile(String uniqueId, String username, String ipAddress, int coins, long lastSeen, long timeJoined) {

        UserProfile userProfile = new UserProfile();
        userProfile.setUniqueId(uniqueId);
        userProfile.setUsername(username);
        userProfile.setIpAddress(ipAddress);
        userProfile.setCoins(coins);
        userProfile.setLastSeen(lastSeen);
        userProfile.setTimeJoined(timeJoined);
        insertProfile(userProfile);
    }

    public void insertProfile(UserProfile userProfile) {
        SQLDatabase database = LightMaster.instance.getDatabase();
        String sql = "INSERT INTO " + TableNames.USER_DATA.getTableName() +
                " (uniqueId, username, ipAddress, coins, lastSeen, timeJoined) VALUES (?, ?, ?, ?, ?, ?)";

        if(database.insertIntoDatabase(sql,
                userProfile.getUniqueId(),
                userProfile.getUsername(),
                userProfile.getIpAddress(),
                userProfile.getCoins(),
                userProfile.getLastSeen(),
                userProfile.getTimeJoined())) {

            LightPrinter.print("User profile inserted successfully.");
        } else {
            throw new RuntimeException("Failed to insert user profile.");
        }
    }

    public boolean userExists(String uniqueId) {
        SQLDatabase database = LightMaster.instance.getDatabase();
        String sql = "SELECT COUNT(*) FROM " + TableNames.USER_DATA.getTableName() + " WHERE uniqueId = ?";
        Integer count = database.queryDatabase(sql, Integer.class, uniqueId);
        return count != null && count > 0;
    }

    public boolean updateBirthdayDate(java.util.Date date, String uniqueId) {
        SQLDatabase database = LightMaster.instance.getDatabase();
        String sql = "UPDATE " + TableNames.USER_DATA.getTableName() + " SET birthday = ? WHERE uniqueId = ?";
        Date sqlDate = new Date(date.getTime());
        return database.insertIntoDatabase(sql, sqlDate, uniqueId);
    }

    public long getBirthdayDate(String uniqueId) {
        SQLDatabase database = LightMaster.instance.getDatabase();
        String sql = "SELECT birthday FROM " + TableNames.USER_DATA.getTableName() + " WHERE uniqueId = ?";
        Date date = database.queryDatabase(sql, Date.class, uniqueId);
        return date != null ? date.getTime() : -1;
    }

    public List<UserProfile> getAllUserProfiles() {
        SQLDatabase database = LightMaster.instance.getDatabase();
        String sql = "SELECT * FROM " + TableNames.USER_DATA.getTableName();
        return database.queryUserProfiles(sql);
    }


    }

