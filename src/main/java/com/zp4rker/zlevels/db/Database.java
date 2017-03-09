package com.zp4rker.zlevels.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zp4rker.zlevels.util.Config;
import com.zp4rker.zlevels.util.ZLogger;

/**
 * @author ZP4RKER
 */
public class Database {

    public static void load() {
        // Send info
        ZLogger.info("Loading database...");
        try {
            // Get ConnectionSource
            ConnectionSource source = getConnection();
            // Load USER_DATA Dao
            Dao<UserData, String> userData = DaoManager.createDao(source, UserData.class);
            // Create USER_DATA
            TableUtils.createTableIfNotExists(source, UserData.class);
            // Check if ratings enabled
            if (Config.RATINGS_ENABLED) {
                // Load STAFF_RATING Dao
                Dao<StaffRating, String> staffRatings = DaoManager.createDao(source, StaffRating.class);
                // Create STAFF_RATING
                TableUtils.createTableIfNotExists(source, StaffRating.class);
            }
            // Close the connection
            source.close();
            // Send info
            ZLogger.info("Successfully loaded Database!");
        } catch (Exception e) {
            e.printStackTrace();
            // Send warning
            ZLogger.warn("Could not load Database!");
        }
    }

    static ConnectionSource getConnection() {
        try {
            // Return new JDBC connection
            return new JdbcConnectionSource("jdbc:mysql://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" +
                    Config.DB_NAME, Config.DB_USER, Config.DB_PASS);
        } catch (Exception e) {
            // Send warning
            ZLogger.warn("Could not get connection to database!");
            // Return null
            return null;
        }
    }

    public static boolean canConnect() {
        // Catch errors
        try {
            // Try connecting
            new JdbcConnectionSource("jdbc:mysql://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" +
                    Config.DB_NAME, Config.DB_USER, Config.DB_PASS).close();
        } catch (Exception e) {
            // Return false
            return false;
        }
        // Return true
        return true;
    }

}
