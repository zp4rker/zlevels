package com.zp4rker.zlevels.util;

import com.zp4rker.zlevels.ZLevels;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

/**
 * @author ZP4RKER
 */
public class Config {

    public static String NAME = "";
    public static String TOKEN = "";
    public static String PREFIX = "";
    public static String SERVER = "";

    public static String DB_HOST = "";
    public static String DB_PORT = "";
    public static String DB_NAME = "";
    public static String DB_USER = "";
    public static String DB_PASS = "";


    static long ERROR_MILLIS = 6000;

    public static boolean AUTOROLE_ENABLED = true;

    public static boolean RATINGS_ENABLED = true;
    public static String STAFF_ROLE = "";
    public static String[] CHANNELS_FOR_RATINGS = {};

    private static void readConfig() throws Exception {
        // Get file
        File file = new File(ZLevels.getDirectory(), "config.json");
        // Get file reader
        FileReader reader = new FileReader(file);
        // Get data as JSON
        JSONObject data = (JSONObject) new JSONParser().parse(reader);

        // Loop through values
        for (Object key : data.keySet()) {
            // Check if empty
            if (data.get(key.toString()).toString().equals("")) throw new ConfigException(new Throwable(key.toString()));
        }

        // Get name
        NAME = data.get("NAME").toString();
        // Get token
        TOKEN = data.get("TOKEN").toString();
        // Get prefix
        PREFIX = data.get("PREFIX").toString();
        // Get server
        SERVER = data.get("SERVER").toString();

        // Get database host
        DB_HOST = data.get("DB_HOST").toString();
        // Get database port
        DB_PORT = data.get("DB_PORT").toString();
        // Get database name
        DB_NAME = data.get("DB_NAME").toString();
        // Get database user
        DB_USER = data.get("DB_USER").toString();
        // Get database pass
        DB_PASS = data.get("DB_PASS").toString();

        // Get error length
        ERROR_MILLIS = Long.parseLong(data.get("ERROR_MILLIS").toString());

        // Get autorole enabled
        AUTOROLE_ENABLED = Boolean.parseBoolean(data.get("AUTOROLE_ENABLED").toString());

        // Get ratings enabled
        RATINGS_ENABLED = Boolean.parseBoolean(data.get("RATINGS_ENABLED").toString());
        // Get staff role
        STAFF_ROLE = data.get("STAFF_ROLE").toString();
        // Get channels for ratings
        CHANNELS_FOR_RATINGS = (String[]) ((JSONArray) data.get("CHANNELS_FOR_RATINGS")).toArray(new String[0]);

        // Send info
        ZLogger.info("Successfully loaded settings from config.");
    }

    public static boolean load() {
        // Catch errors
        try {
            // Get file
            File file = new File(ZLevels.getDirectory(), "config.json");
            // Check if file exists
            if (!file.exists()) {
                // Send info
                ZLogger.info("No config exists, creating default config...");
                // Create the file
                file.createNewFile();
                // Get file writer
                FileWriter writer =  new FileWriter(file);
                // Write default config
                writer.write(getDefaultConfig());
                // Flush writer
                writer.flush();
                // Close writer
                writer.close();
                // Return false
                return false;
            }
            // Load config
            readConfig();
            // Return true
            return true;
        } catch (Exception e) {
            // Check if ConfigException
            if (e instanceof ConfigException) {
                // Send warning
                ZLogger.warn(e.getCause().getMessage() + " can not be empty!");
            } else {
                // Send warning
                ZLogger.warn("Could not load config!");
            }
            // Return false
            return false;
        }
    }

    private static String getDefaultConfig() {
        // Catch errors
        try {
            // Get input stream
            InputStream stream = ZLevels.class.getResourceAsStream("/config.json");
            // Return string
            return fromStream(stream);
        } catch (Exception e) {
            // Send warning
            ZLogger.warn("Could not read template config!");
            // Return null
            return null;
        }
    }

    private static String fromStream(InputStream stream) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = stream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        stream.close();
        return result.toString("UTF-8");
    }

}

class ConfigException extends Exception {

    ConfigException(Throwable throwable) {
        super(throwable);
    }

}
