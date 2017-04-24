package com.zp4rker.dscrd.zlevels.core.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zp4rker.dscrd.zlevels.core.config.Config;
import com.zp4rker.dscrd.core.logger.ZLogger;

/**
 * @author ZP4RKER
 */
public class Database {

    private static ConnectionSource source = null;

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
            // Send info
            ZLogger.info("Successfully loaded Database!");
        } catch (Exception e) {
            e.printStackTrace();
            // Send warning
            ZLogger.warn("Could not load Database!");
        }
    }

    static ConnectionSource getConnection() {
        if (source == null) {
            try {
                // Get new JDBC connection
                JdbcPooledConnectionSource source = new JdbcPooledConnectionSource("jdbc:mysql://" + Config.DB_HOST + ":" +
                        Config.DB_PORT + "/" + Config.DB_NAME, Config.DB_USER, Config.DB_PASS);
                // Set check interval
                source.setCheckConnectionsEveryMillis(5 * 1000);
                // Set connection
                Database.source = source;
            } catch (Exception e) {
                // Send warning
                ZLogger.warn("Could not get connection to database!");
                // Return null
                return null;
            }
        }
        // Return source
        return source;
    }

    static void closeConnection() {
        try {
            // Close connection
            source.close();
            // Set source to null
            source = null;
        } catch (Exception e) {
            // Send warning
            ZLogger.warn("Could not close connection to database!");
        }
    }

    public static boolean canConnect() {
        // Catch errors
        try {
            // Try connecting
            new JdbcPooledConnectionSource("jdbc:mysql://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" +
                    Config.DB_NAME, Config.DB_USER, Config.DB_PASS).close();
        } catch (Exception e) {
            // Return false
            return false;
        }
        // Return true
        return true;
    }

}
