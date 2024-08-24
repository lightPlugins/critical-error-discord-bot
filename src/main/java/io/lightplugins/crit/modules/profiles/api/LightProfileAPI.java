package io.lightplugins.crit.modules.profiles.api;

import io.lightplugins.crit.modules.profiles.impl.UserProfile;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.database.SQLDatabase;
import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.util.database.model.TableNames;
import lombok.Getter;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class LightProfileAPI {

    private List<UserProfile> userProfiles = new ArrayList<>();

    public void createNewProfile(String uniqueId, String username, boolean currentlyBanned, int coins, long lastSeen, long timeJoined) {

        UserProfile userProfile = new UserProfile();
        userProfile.setUniqueId(uniqueId);
        userProfile.setUsername(username);
        userProfile.setCurrentlyBanned(currentlyBanned);
        userProfile.setCoins(coins);
        userProfile.setLastSeen(lastSeen);
        userProfile.setTimeJoined(timeJoined);
        insertProfile(userProfile);
    }

    public void syncUserProfilesToRAM() {
        userProfiles = getAllUserProfilesFromDatabase();
    }

    public void insertProfile(UserProfile userProfile) {
        SQLDatabase database = LightMaster.instance.getDatabase();
        String sql = "INSERT INTO " + TableNames.USER_DATA.getTableName() +
                " (uniqueId, username, currentlyBanned, coins, lastSeen, timeJoined) VALUES (?, ?, ?, ?, ?, ?)";

        if(database.insertIntoDatabase(sql,
                userProfile.getUniqueId(),
                userProfile.getUsername(),
                userProfile.isCurrentlyBanned(),
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

    // Modify the getAllUserProfilesFromDatabase method to handle date parsing correctly
    public List<UserProfile> getAllUserProfilesFromDatabase() {
        SQLDatabase database = LightMaster.instance.getDatabase();
        String sql = "SELECT ud.*, ct.channelId, ct.activeTime " +
                "FROM " + TableNames.USER_DATA.getTableName() + " ud " +
                "LEFT JOIN " + TableNames.CHANNEL_TIME.getTableName() + " ct " +
                "ON ud.uniqueId = ct.uniqueId";
        List<UserProfile> userProfiles;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            Map<String, UserProfile> userProfileMap = new HashMap<>();
            while (resultSet.next()) {
                String uniqueId = resultSet.getString("uniqueId");
                UserProfile userProfile = userProfileMap.get(uniqueId);
                if (userProfile == null) {
                    userProfile = new UserProfile();
                    userProfile.setUniqueId(uniqueId);
                    userProfile.setUsername(resultSet.getString("username"));
                    userProfile.setCurrentlyBanned(resultSet.getBoolean("currentlyBanned"));
                    userProfile.setCoins(resultSet.getInt("coins"));
                    userProfile.setLastSeen(resultSet.getLong("lastSeen"));
                    userProfile.setTimeJoined(resultSet.getLong("timeJoined"));
                    // Correctly parse the birthday field
                    long birthdayTimestamp = resultSet.getLong("birthday");
                    if (!resultSet.wasNull()) {
                        userProfile.setBirthday(new java.util.Date(birthdayTimestamp));
                    }
                    userProfile.setActiveTime(new HashMap<>());
                    userProfileMap.put(uniqueId, userProfile);
                }
                String channelId = resultSet.getString("channelId");
                if (channelId != null) {
                    double activeTime = resultSet.getDouble("activeTime");
                    userProfile.getActiveTime().put(channelId, activeTime);
                }
            }
            userProfiles = new ArrayList<>(userProfileMap.values());
        } catch (SQLException e) {
            throw new RuntimeException("Could not query user profiles from database", e);
        }
        return userProfiles;
    }

    public UserProfile getUserProfileFromDatabase(String uniqueId) {
        SQLDatabase database = LightMaster.instance.getDatabase();
        String sql = "SELECT * FROM " + TableNames.USER_DATA.getTableName() + " WHERE uniqueId = " + uniqueId;
        List<UserProfile> userProfiles = database.queryUserProfiles(sql);
        return userProfiles.isEmpty() ? null : userProfiles.getFirst();
    }

    public UserProfile getUserProfile(String uniqueId) {
        for (UserProfile userProfile : userProfiles) {
            if (userProfile.getUniqueId().equals(uniqueId)) {
                return userProfile;
            }
        }
        return null;
    }

    public void addCurrentlyBannedColumn() {
        SQLDatabase database = LightMaster.instance.getDatabase();
        String alterTableSql = "ALTER TABLE " + TableNames.USER_DATA.getTableName() + " ADD COLUMN currentlyBanned INT DEFAULT 0";
        database.executeSQL(alterTableSql);
    }

}


