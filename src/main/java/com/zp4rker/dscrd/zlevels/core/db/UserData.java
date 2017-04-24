package com.zp4rker.dscrd.zlevels.core.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.zp4rker.dscrd.zlevels.core.config.Config;
import com.zp4rker.dscrd.zlevels.ZLevels;
import com.zp4rker.dscrd.zlevels.core.util.AutoRole;
import com.zp4rker.dscrd.core.logger.ZLogger;
import com.zp4rker.dscrd.zlevels.core.util.LevelsUtil;
import net.dv8tion.jda.core.entities.User;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * @author ZP4RKER
 */
@DatabaseTable(tableName = "USER_DATA")
public class UserData {

    @DatabaseField(id = true, unique = true, canBeNull = false) private String userId;

    @DatabaseField(canBeNull = false) private String username;

    @DatabaseField(canBeNull = false) private String avatarUrl;

    @DatabaseField(canBeNull = false) private long totalXp = 0;

    @DatabaseField(canBeNull = false) private int level = 0;

    // Temp data
    private static UserData data = null;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        // Get user
        User user = ZLevels.jda.getUserById(getUserId());
        // Set username and discriminator
        setUsername(user.getName() + "#" + user.getDiscriminator());
        // Return username
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        // Get User
        User user = ZLevels.jda.getUserById(getUserId());
        // Set avatar url
        setAvatarUrl(user.getEffectiveAvatarUrl());
        // Return avatar url
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public long getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(long totalXp) {
        // Set to field
        this.totalXp = totalXp;
        // Get and set levels
        setLevel(LevelsUtil.xpToLevels(totalXp));
        // Check if autorole enabled
        if (Config.AUTOROLE_ENABLED) {
            // Auto-assign role
            AutoRole.assignRole(this);
        }
    }

    public int getLevel() {
        return level;
    }

    private void setLevel(int level) {
        // Check if new level
        if (level > this.level) {
            // Send info
            ZLogger.info(getUsername() + " just levelled up to level " + level + "!");
            // Get User
            User user = ZLevels.jda.getUserById(getUserId());
            // Catch errors
            try {
                // Open DM
                user.openPrivateChannel().complete();
            } catch (Exception e) {
                // Send warning
                ZLogger.warn("Couldn't open DM or already open!");
            }
            // Send DM
            user.getPrivateChannel().sendMessage("Congratulations, you are now level " + level + "!").queue();
        }
        // Set level
        this.level = level;
    }

    public void save() {
        // Get current data
        UserData current = this;
        // Update avatar url
        current.setAvatarUrl(current.getAvatarUrl());
        // Update username
        current.setUsername(current.getUsername());
        // Run asynchronously
        ZLevels.async.submit(() -> {
            try {
                // Get the connection
                ConnectionSource source = Database.getConnection();
                // Get the Dao
                Dao<UserData, String> db = DaoManager.createDao(source, UserData.class);
                // Save the record
                db.createOrUpdate(current);
                // Close connection
                Database.closeConnection();
            } catch (Exception e) {
                // Send warning
                ZLogger.warn("Could not save UserData for " + getUserId() + "!");
                e.printStackTrace();
            }
        });
    }

    public void delete() {
        // Get current data
        UserData current = this;
        // Run asynchronously
        ZLevels.async.submit(() -> {
            try {
                // Get the connection
                ConnectionSource source = Database.getConnection();
                // Get the Dao
                Dao<UserData, String> db = DaoManager.createDao(source, UserData.class);
                // Delete the record
                db.delete(current);
                // Close connection
                Database.closeConnection();
            } catch (Exception e) {
                // Send warning
                ZLogger.warn("Colud not delete UserData for " + getUserId() + "!");
            }
        });
    }

    public static UserData fromId(String userId) {
        // Get data
        UserData data = byId(userId);
        // Set data to null
        UserData.data = null;
        // Return data
        return data;
    }

    private static UserData byId(String id) {
        try {
            // Get the connection
            ConnectionSource source = Database.getConnection();
            // Get the Dao
            Dao<UserData, String> db = DaoManager.createDao(source, UserData.class);
            // Search
            data = db.queryForId(id);
            // Close connection
            Database.closeConnection();
        } catch (Exception e) {
            // Check if array error
            if (e instanceof IndexOutOfBoundsException) {
                // Return null
                return null;
            }
            // Send warning
            ZLogger.warn("Could not get UserData for " + id + "!");
            e.printStackTrace();
        }
        // Return data
        return data;
    }

    public static List<UserData> getAllData() {
        try {
            // Get the connection
            ConnectionSource source = Database.getConnection();
            // Get the Dao
            Dao<UserData, String> db = DaoManager.createDao(source, UserData.class);
            // Get list of data
            List<UserData> dataList = db.queryForAll();
            // Sort list
            dataList.sort((data1, data2) -> {
                // Check if equal
                if (data1.getTotalXp() == data2.getTotalXp()) return 0;
                // Return higher value
                return data1.getTotalXp() < data2.getTotalXp() ? 1 : -1;
            });
            // Close connection
            Database.closeConnection();
            // Return list
            return dataList;
        } catch (Exception e) {
            // Send warning
            ZLogger.warn("Could not get all data!");
        }
        // Return null
        return null;
    }

    public int[] getRank() {
        // Get current data
        UserData current = this;
        // Create array
        int[] rank = new int[2];
        // Get data list
        List<UserData> dataList = getAllData();
        // Add count to array
        rank[1] = dataList.size();
        // Add rank to array
        rank[0] = getPosition(dataList, current) + 1;
        return rank;
    }

    private int getPosition(List<UserData> list, UserData search) {
        // Loop through each data
        for (UserData data : list) {
            // Check if matches id
            if (search.getUserId().equals(data.getUserId())) return list.indexOf(data);
        }
        return 0;
    }

    public static UserData fromRank(int rank) {
        // Get index
        int index = rank - 1;
        // Get list
        List<UserData> dataList = getAllData();
        // Return at index
        return dataList.get(index);
    }

}
