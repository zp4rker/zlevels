package me.zp4rker.zlevels.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.zlevels.ZLevels;
import me.zp4rker.zlevels.config.Config;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The StaffRating database class.
 *
 * @author ZP4RKER
 */
@DatabaseTable(tableName = "STAFF_RATING")
public class StaffRating {

    @DatabaseField(generatedId = true, unique = true) private int id;

    @DatabaseField(canBeNull = false, unique = true) private String userId;

    @DatabaseField(canBeNull = false) private int ratings = 0;

    @DatabaseField(canBeNull = false) private int monthlyRatings = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String getUserId() {
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

    /**
     * Saves this rating to the database.
     */
    public void save() {
        StaffRating current = this;

        ZLevels.async.submit(() -> {
            try {
                ConnectionSource source = Database.openConnection();
                Dao<StaffRating, String> db = DaoManager.createDao(source, StaffRating.class);

                db.createOrUpdate(current);
                Database.closeConnection();
            } catch (Exception e) {
                ZLogger.warn("Could not save StaffRating for " + getUserId() + "!");
            }
        });
    }

    /**
     * Delete this rating from the database.
     */
    public void delete() {
        StaffRating current = this;

        ZLevels.async.submit(() -> {
            try {
                ConnectionSource source = Database.openConnection();
                Dao<StaffRating, String> db = DaoManager.createDao(source, StaffRating.class);

                db.delete(current);
                Database.closeConnection();
            } catch (Exception e) {
                ZLogger.warn("Could not delete StaffRating of " + getUserId() + "!");
            }
        });
    }

    /**
     * Gets the staff rating by userId from the database.
     *
     * @param userId The user id.
     * @return The staff rating.
     */
    public static StaffRating fromId(String userId) {
        try {
            ConnectionSource source = Database.openConnection();
            Dao<StaffRating, String> db = DaoManager.createDao(source, StaffRating.class);

            StaffRating data = db.queryForEq("userId", userId).get(0);
            Database.closeConnection();

            return data;
        } catch (Exception e) {
            ZLogger.warn("Could not get UserData for " + userId + "!");

            return null;
        }
    }

    /**
     * Gets a list of all staff ratings from the database.
     *
     * @return The list of staff ratings.
     */
    private static List<StaffRating> getAllData() {
        try {
            ConnectionSource source = Database.openConnection();
            Dao<StaffRating, String> db = DaoManager.createDao(source, StaffRating.class);

            List<StaffRating> dataList = db.queryForAll();

            dataList.sort((data1, data2) -> {
                if (data1.getRatings() == data2.getRatings()) return 0;

                return data1.getRatings() < data2.getRatings() ? 1 : -1;
            });

            Database.closeConnection();

            return dataList;
        } catch (Exception e) {
            ZLogger.warn("Could not get all data!");

            return null;
        }
    }

    /**
     * Start the monthly timer for staff ratings.
     */
    public static void startMonth() {
        if (!Config.RATINGS_ENABLED) return;

        getAllData().forEach(data -> {
            data.setMonthlyRatings(0);
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startMonth();
            }
        }, 2592000000L);
    }

}
