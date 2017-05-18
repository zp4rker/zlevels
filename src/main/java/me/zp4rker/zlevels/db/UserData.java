package me.zp4rker.zlevels.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.zlevels.ZLevels;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.util.AutoRole;
import me.zp4rker.zlevels.util.LevelsUtil;
import net.dv8tion.jda.core.entities.User;

import java.util.*;

/**
 * The UserData database class.
 *
 * @author ZP4RKER
 */
@DatabaseTable(tableName = "USER_DATA")
public class UserData {

    @DatabaseField(unique = true, generatedId = true) private int id;

    @DatabaseField(unique = true, canBeNull = false) private String userId;

    @DatabaseField(canBeNull = false) private String username;

    @DatabaseField(canBeNull = false) private String avatarUrl;

    @DatabaseField(canBeNull = false) private long totalXp = 0;

    @DatabaseField(canBeNull = false) private int level = 0;

    /**
     * Gets the id of the record.
     *
     * @return The id.
     */
    private int getId() {
        return id;
    }

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
                ConnectionSource source = Database.openConnection();
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
                ConnectionSource source = Database.openConnection();
                Dao<UserData, String> db = DaoManager.createDao(source, UserData.class);

                db.createOrUpdate(data);

                Database.closeConnection();
            } catch (Exception e) {
                ZLogger.warn("Could not save UserData for " + data.getUserId() + "!");
                e.printStackTrace();
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
                ConnectionSource source = Database.openConnection();
                Dao<UserData, String> db = DaoManager.createDao(source, UserData.class);

                db.delete(current);

                Database.closeConnection();
            } catch (Exception e) {
                ZLogger.warn("Colud not delete UserData for " + getUserId() + "!");
                e.printStackTrace();
            }
        });
    }

    /**
     * Gets the user data by userId from the cache and database.
     *
     * @param userId The user id.
     * @return The user data.
     */
    public static UserData fromId(String userId) {
        if (cache.containsKey(userId)) {
            ZLogger.debug("From cache.");
            return cache.get(userId);
        }
        ZLogger.debug("From Database.");
        try {
            ConnectionSource source = Database.openConnection();
            Dao<UserData, String> db = DaoManager.createDao(source, UserData.class);

            UserData data = db.queryForEq("userId", userId).get(0);

            Database.closeConnection();

            return data;
        } catch (Exception e) {
            if (e instanceof IndexOutOfBoundsException) {
                return null;
            }

            ZLogger.warn("Could not get UserData for " + userId + "!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets a list of all the user data from the Database and cache.
     *
     * @return The list of all user data.
     */
    public static List<UserData> getAllData() {
        try {
            ConnectionSource source = Database.openConnection();
            Dao<UserData, String> db = DaoManager.createDao(source, UserData.class);

            List<UserData> dataList = new ArrayList<>();
            for (UserData data : db) {
                dataList.add(data);
            }

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
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the index of the user by userId in a specified list.
     *
     * @param userId The user id.
     * @param list The list of user data.
     * @return The index of the user data.
     */
    private static int indexOfUser(String userId, List<UserData> list) {
        for (UserData data : list) {
            if (data.getUserId().equals(userId)) return list.indexOf(data);
        }

        return -1;
    }

    /**
     * Gets the rank of the current user data.
     *
     * @return The rank.
     */
    public int[] getRank() {
        UserData current = this;

        int[] rank = new int[2];

        List<UserData> dataList = getAllData();

        rank[1] = dataList.size();
        rank[0] = getPosition(dataList, current) + 1;

        return rank;
    }

    /**
     * Gets the position of the user data in a list.
     *
     * @param list The list of user data.
     * @param search The user data.
     * @return The position in the list.
     */
    private int getPosition(List<UserData> list, UserData search) {
        for (UserData data : list) {
            if (search.getUserId().equals(data.getUserId())) return list.indexOf(data);
        }

        return 0;
    }

    /**
     * Gets the user data by rank.
     *
     * @param rank The rank.
     * @return The user data.
     */
    public static UserData fromRank(int rank) {
        int index = rank - 1;

        List<UserData> dataList = getAllData();

        return dataList.get(index);
    }

    /**
     * Starts the timer to flush cache.
     */
    public static void startFlushTimer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                flushCache();
            }
        }, 3600000, 3600000);
    }

}
