package com.zp4rker.dscrd.zlevels.core.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.zp4rker.dscrd.zlevels.ZLevels;
import com.zp4rker.dscrd.zlevels.core.config.Config;
import com.zp4rker.dscrd.core.logger.ZLogger;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

/**
 * @author ZP4RKER
 */
@DatabaseTable(tableName = "STAFF_RATING")
public class StaffRating {

    @DatabaseField(generatedId = true, unique = true) private int id;

    @DatabaseField(canBeNull = false, unique = true) private String userId;

    @DatabaseField(canBeNull = false) private int ratings = 0;

    @DatabaseField(canBeNull = false) private int monthlyRatings = 0;

    // Temp data
    private static StaffRating data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }

    public int getMonthlyRatings() {
        return monthlyRatings;
    }

    public void setMonthlyRatings(int monthlyRatings) {
        this.monthlyRatings = monthlyRatings;
    }

    public void save() {
        // Get current data
        StaffRating current = this;
        // Run asynchronously
        ZLevels.async.submit(() -> {
            try {
                // Get the connection
                ConnectionSource source = Database.getConnection();
                // Get the Dao
                Dao<StaffRating, String> db = DaoManager.createDao(source, StaffRating.class);
                // Save the record
                db.createOrUpdate(current);
                // Close connection
                Database.closeConnection();
            } catch (Exception e) {
                // Send warning
                ZLogger.warn("Could not save StaffRating for " + getUserId() + "!");
            }
        });
    }

    public void delete() {
        // Get current data
        StaffRating current = this;
        // Run asynchronously
        ZLevels.async.submit(() -> {
            try {
                // Get the connection
                ConnectionSource source = Database.getConnection();
                // Get the Dao
                Dao<StaffRating, String> db = DaoManager.createDao(source, StaffRating.class);
                // Save the record
                db.delete(current);
                // Close connection
                Database.closeConnection();
            } catch (Exception e) {
                // Send warning
                ZLogger.warn("Could not delete StaffRating of " + getUserId() + "!");
            }
        });
    }

    public static StaffRating fromId(String userId) {
        // Get data
        StaffRating data = byId(userId);
        // Set data to null
        StaffRating.data = null;
        // Return data
        return data;
    }

    private static StaffRating byId(String id) {
        try {
            // Get the connection
            ConnectionSource source = Database.getConnection();
            // Get the Dao
            Dao<StaffRating, String> db = DaoManager.createDao(source, StaffRating.class);
            // Search
            data = db.queryForEq("userId", id).get(0);
            // Close connection
            Database.closeConnection();
        } catch (Exception e) {
            // Send warning
            ZLogger.warn("Could not get UserData for " + id + "!");
        }
        // Return data
        return data;
    }

    private static List<StaffRating> getAllData() {
        try {
            // Get the connection
            ConnectionSource source = Database.getConnection();
            // Get the Dao
            Dao<StaffRating, String> db = DaoManager.createDao(source, StaffRating.class);
            // Get list of data
            List<StaffRating> dataList = db.queryForAll();
            // Sort list
            dataList.sort((data1, data2) -> {
                // Check if equal
                if (data1.getRatings() == data2.getRatings()) return 0;
                // Return higher value
                return data1.getRatings() < data2.getRatings() ? 1 : -1;
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

    public static void startMonth() {
        // Check if enabled
        if (!Config.RATINGS_ENABLED) return;
        // Loop through all data
        getAllData().forEach(data -> {
            // Clear data
            data.setMonthlyRatings(0);
        });
        // Start timer
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // Start month
                startMonth();
            }
        }, 2592000000L);
    }

}
