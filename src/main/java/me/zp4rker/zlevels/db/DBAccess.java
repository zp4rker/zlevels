package me.zp4rker.zlevels.db;

import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.zlevels.config.Config;

import java.sql.*;

/**
 * Database connection class.
 *
 * @author ZP4RKER
 */
public class DBAccess {

    private static Connection connection = null;
    public static Statement statement = null;
    public static PreparedStatement prepStatement = null;
    public static ResultSet resultSet = null;

    private static void openConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" +
                Config.DB_NAME, Config.DB_USER, Config.DB_PASS);
    }

    public static void createStatement() throws SQLException {
        statement = connection.createStatement();
    }

    public static void prepareStatement(String sql) throws SQLException {
        prepStatement = connection.prepareStatement(sql);
    }

    private static void close() {
        try {
            if (resultSet != null) resultSet.close();

            if (statement != null) statement.close();
            if (prepStatement != null) prepStatement.close();

            if (connection != null) connection.close();
        } catch (Exception e) {
            ZLogger.warn("Error: " + e.getMessage());
        }
    }

    public static boolean testConnection() {
        try {
            openConnection();

            createStatement();

            statement.execute("select * from USER_DATA");

            return true;
        } catch (Exception e) {
            ZLogger.warn("Error: " + e.getMessage());

            return false;
        } finally {
            close();
        }
    }

}
