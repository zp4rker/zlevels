package me.zp4rker.dscrd.zlevels.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import me.zp4rker.dscrd.core.logger.ZLogger;
import me.zp4rker.dscrd.zlevels.ZLevels;
import me.zp4rker.dscrd.zlevels.config.Config;
import me.zp4rker.dscrd.zlevels.util.AutoRole;
import me.zp4rker.dscrd.zlevels.util.LevelsUtil;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.List;

/**
 * The UserData database class.
 *
 * @author ZP4RKER
 */
@DatabaseTable(tableName = "USER_DATA")
public class UserData {

    @DatabaseField(id = true, unique = true, canBeNull = false) private String userId;

    @DatabaseField(canBeNull = false) private String username;

    @DatabaseField(canBeNull = false) private String avatarUrl;

    @DatabaseField(canBeNull = false) private long totalXp = 0;

    @DatabaseField(canBeNull = false) private int level = 0;

    /**
     * Cached UserData.
     */
    private static HashMap<String, UserData> cache = new HashMap<>();

    /**
     * Gets the user id.
     *
     * @return The user id.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     *
     * @param userId The user id.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the username.
     *
     * @return The username.
     */
    public String getUsername() {
        // Get user
        User user = ZLevels.jda.getUserById(getUserId());
        // Set username and discriminator
        setUsername(user.getName() + "#" + user.getDiscriminator());
        // Return username
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username The username.
     */
    private void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the avatar url.
     *
     * @return The avatar url.
     */
    public String getAvatarUrl() {
        // Get User
        User user = ZLevels.jda.getUserById(getUserId());
        // Set avatar url
        setAvatarUrl(user.getEffectiveAvatarUrl());
        // Return avatar url
        return avatarUrl;
    }

    /**
     * Sets the avatar url.
     *
     * @param avatarUrl The avatar url.
     */
    private void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * Gets the total XP.
     *
     * @return The total XP.
     */
    public long getTotalXp() {
        return totalXp;
    }

    /**
     * Sets the total XP.
     *
     * @param totalXp The total XP.
     */
    public void setTotalXp(long totalXp) {
        this.totalXp = totalXp;

        setLevel(LevelsUtil.xpToLevels(totalXp));

        if (Config.AUTOROLE_ENABLED) {
            AutoRole.assignRole(this);
        }
    }

    /**
     * Gets the level.
     *
     * @return The level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the level.
     *
     * @param level The level.
     */
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

    /**
     * Saves this user data to the cache.
     */
    public void save() {
        /*// Get current data
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
        });*/
        // Check if in cache already
        if (cache.containsKey(getUserId())) cache.replace(getUserId(), this);
        // Add to cache
        cache.put(getUserId(), this);
    }

    /**
     * Saves the user data to the database.
     *
     * @param data The user data to save.
     */
    private static void save(UserData data) {
        data.setAvatarUrl(data.getAvatarUrl());
        data.setUsername(data.getUsername());

        ZLevels.async.submit(() -> {
            try {
                ConnectionSource source = Database.getConnection();
                Dao<UserData, String> db = DaoManager.createDao(source, UserData.class);

                db.createOrUpdate(data);

                Database.closeConnection();
            } catch (Exception e) {
                ZLogger.warn("Could not save UserData for " + data.getUserId() + "!");
            }
        });
    }

    /**
     * Uploads all the cache to the database, and clears the cache.
     */
    public static void flushCache() {
        cache.values().forEach(data -> save(data));
        cache.clear();
    }

    /**
     * Deletes this user data from the database and cache.
     */
    public void delete() {
        UserData current = this;

        if (cache.containsKey(current.getUserId())) cache.remove(current.getUserId());

        ZLevels.async.submit(() -> {
            try {
                ConnectionSource source = Database.getConnection();
                Dao<UserData, String> db = DaoManager.createDao(source, UserData.class);

                db.delete(current);

                Database.closeConnection();
            } catch (Exception e) {
                ZLogger.warn("Colud not delete UserData for " + getUserId() + "!");
            }
        });
    }

    /**
     * Gets the user data by userId from cache or database.
     *
     * @param userId The user id.
     * @return The user data.
     */
    public static UserData fromId(String userId) {
        if (cache.containsKey(userId)) {
            return cache.get(userId);
        }

        return byId(userId);
    }

    private static UserData byId(String id) {
        try {
            ConnectionSource source = Database.getConnection();
            Dao<UserData, String> db = DaoManager.createDao(source, UserData.class);

            UserData data = db.queryForId(id);

            Database.closeConnection();

            return data;
        } catch (Exception e) {
            if (e instanceof IndexOutOfBoundsException) {
                return null;
            }

            ZLogger.warn("Could not get UserData for " + id + "!");

            return null;
        }
    }

    public static List<UserData> getAllData() {
        try {
            ConnectionSource source = Database.getConnection();
            Dao<UserData, String> db = DaoManager.createDao(source, UserData.class);

            List<UserData> dataList = db.queryForAll();

            for (UserData data : cache.values()) {
                if (indexOfUser(data.getUserId(), dataList) < 0) continue;

                dataList.set(indexOfUser(data.getUserId(), dataList), data);
            }

            dataList.sort((data1, data2) -> {
                if (data1.getTotalXp() == data2.getTotalXp()) return 0;

                return data1.getTotalXp() < data2.getTotalXp() ? 1 : -1;
            });

            Database.closeConnection();

            return dataList;
        } catch (Exception e) {
            ZLogger.warn("Could not get all data!");

            return null;
        }
    }

    private static int indexOfUser(String userId, List<UserData> list) {
        for (UserData data : list) {
            if (data.getUserId().equals(userId)) return list.indexOf(data);
        }

        return -1;
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
