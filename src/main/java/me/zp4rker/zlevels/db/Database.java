package me.zp4rker.zlevels.db;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.zlevels.config.Config;

/**
 * The database connection class.
 *
 * @author ZP4RKER
 */
public class Database {

    private static ConnectionSource source = null;

    /**
     * Loads the database.
     */
    public static void load() {
        ZLogger.info("Loading db...");

        try {
            ConnectionSource source = openConnection();
            DaoManager.createDao(source, UserData.class);

            TableUtils.createTableIfNotExists(source, UserData.class);

            ZLogger.info("Successfully loaded Database!");
        } catch (Exception e) {
            ZLogger.warn("Could not load Database!");
        }
    }

    /**
     * Opens the connection to the database.
     *
     * @return The connection.
     */
    static ConnectionSource openConnection() {
        if (source == null) {
            try {
                JdbcPooledConnectionSource source = new JdbcPooledConnectionSource("jdbc:mysql://" + Config.DB_HOST + ":" +
                        Config.DB_PORT + "/" + Config.DB_NAME, Config.DB_USER, Config.DB_PASS);
                source.setCheckConnectionsEveryMillis(5 * 1000);

                Database.source = source;

                return source;
            } catch (Exception e) {
                ZLogger.warn("Could not get connection to db!");
                e.printStackTrace();
                return null;
            }
        }

        return source;
    }

    /**
     * Close the connection to the database.
     */
    static void closeConnection() {
        try {
            source.close();

            source = null;
        } catch (Exception e) {
            ZLogger.warn("Could not close connection to db!");
            e.printStackTrace();
        }
    }

    /**
     * Check if the bot can connect to the database.
     *
     * @return Whether or not the bot can connect to the database.
     */
    public static boolean canConnect() {
        try {
            new JdbcPooledConnectionSource("jdbc:mysql://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" +
                    Config.DB_NAME, Config.DB_USER, Config.DB_PASS).close();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

}
