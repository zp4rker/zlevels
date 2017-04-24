package com.zp4rker.dscrd.zlevels.core.config;

import com.zp4rker.dscrd.zlevels.ZLevels;
import com.zp4rker.dscrd.zlevels.core.util.AutoRole;
import com.zp4rker.dscrd.core.logger.ZLogger;
import com.zp4rker.dscrd.core.yaml.file.Yaml;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ZP4RKER
 */
public class Config {

    public static String NAME = "";
    public static String TOKEN = "";
    public static String PREFIX = "";
    public static String SERVER = "";
    public static List<String> OPS = Arrays.asList("145064570237485056");
    public static String EMBED_COLOUR = "";
    public static String GAME_STATUS = "";

    public static String DB_HOST = "";
    public static String DB_PORT = "";
    public static String DB_NAME = "";
    public static String DB_USER = "";
    public static String DB_PASS = "";


    public static long ERROR_LENGTH = 6000;

    public static boolean AUTOROLE_ENABLED = true;

    public static boolean RATINGS_ENABLED = true;
    public static String STAFF_ROLE = "";
    public static List<String> CHANNELS_FOR_RATINGS = new ArrayList<>();

    public static boolean load() {
        // Catch errors
        try {
            // Get file
            File file = new File(ZLevels.getDirectory(), "config.yml");
            // Check if file exists
            if (!file.exists()) {
                // Check if config.json exists
                if (new File(file.getParentFile(), "config.json").exists()) {
                    // Send info
                    ZLogger.info("Old config exists, getting data from old config...");
                    // Transfer from json
                    transferFromJSON();
                    // Send info
                    ZLogger.info("Now deleting old config...");
                    // Delete config.json
                    new File(file.getParentFile(), "config.json").delete();
                    // Send info
                    ZLogger.info("Successfully deleted old config.");
                } else {
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
            }
            // Load config
            readConfig();
            // Return true
            return true;
        } catch (ConfigException e) {
            // Send warning
            ZLogger.warn(e.getCause().getMessage() + " can not be empty!");
            // Return false
            return false;
        } catch (Exception e) {
            // Send warning
            ZLogger.warn("Could not load config!");
            e.printStackTrace();
            // Return false
            return false;
        }
    }

    private static void readConfig() throws Exception {
        // Get file
        File file = new File(ZLevels.getDirectory(), "config.yml");
        // Get data
        Yaml data = Yaml.loadConfiguration(file);

        // Loop through fields
        for (Field field : Config.class.getDeclaredFields()) {
            // Get config key
            String key = getNewKey(field.getName());
            // Check if key is null
            if (key == null) continue;
            // Check if empty or null
            if (data.getString(key) == null || data.getString(key).isEmpty()) {
                // Check if GAME_STATUS
                if (data.getString(key) != null && key.equals("basic-settings.game-status")) continue;
                // Check if STAFF_ROLE or CHANNELS_FOR_RATINGS
                if (key.equals("staff-ratings.staff-role") || key.equals("staff-ratings.staff-role")) {
                    // Check if ratings enabled
                    if (!data.getBoolean("staff-ratings.enabled")) continue;
                }
                // Create node
                data.set(key, "");
                // Save new data
                data.save(file);
                // Throw exception
                throw new ConfigException(new Throwable(key));
            }
        }

        // Get name
        NAME = data.getString("basic-settings.name");
        // Get token
        TOKEN = data.getString("basic-settings.token");
        // Get prefix
        PREFIX = data.getString("basic-settings.prefix");
        // Get server
        SERVER = data.getString("basic-settings.server");
        // Get ops
        OPS = data.getStringList("basic-settings.ops");
        // Get embed colour
        EMBED_COLOUR = data.getString("basic-settings.embed-colour");
        // Get game status
        GAME_STATUS = data.getString("basic-settings.game-status");

        // Get database host
        DB_HOST = data.getString("database.host");
        // Get database port
        DB_PORT = data.getString("database.port");
        // Get database name
        DB_NAME = data.getString("database.name");
        // Get database user
        DB_USER = data.getString("database.user");
        // Get database pass
        DB_PASS = data.getString("database.pass");

        // Get error length
        ERROR_LENGTH = data.getLong("more-settings.error-length");
        // Get autorole enabled
        AUTOROLE_ENABLED = data.getBoolean("more-settings.autorole-enabled");

        // Get ratings enabled
        RATINGS_ENABLED = data.getBoolean("staff-ratings.enabled");
        // Get staff role
        STAFF_ROLE = data.getString("staff-ratings.staff-role");
        // Get channels
        CHANNELS_FOR_RATINGS = data.getStringList("staff-ratings.channels");

        // Send info
        ZLogger.info("Successfully loaded settings from config.");
    }

    private static void transferFromJSON() throws Exception {
        // Get file
        File file = new File(ZLevels.getDirectory(), "config.json");
        // Get file reader
        FileReader reader = new FileReader(file);
        // Get data as JSON
        JSONObject data = (JSONObject) new JSONParser().parse(reader);
        // Create new YAML instance
        Yaml yaml = Yaml.loadConfiguration(new InputStreamReader(ZLevels.class.getResourceAsStream("/config.yml")));

        // Loop through values
        for (Object key : data.keySet()) {
            // Get value
            Object value = data.get(key.toString());
            // Check which key
            switch (key.toString()) {
                case "NAME":
                case "TOKEN":
                case "PREFIX":
                case "SERVER":
                case "DB_HOST":
                case "DB_PORT":
                case "DB_NAME":
                case "DB_USER":
                case "DB_PASS":
                case "STAFF_ROLE":
                    yaml.set(getNewKey(key.toString()), value.toString());
                    break;
                case "OPS":
                case "CHANNELS_FOR_RATINGS":
                    yaml.set(getNewKey(key.toString()), ((JSONArray) value).toArray(new String[0]));
                    break;
                case "AUTOROLE_ENABLED":
                case "RATINGS_ENABLED":
                    yaml.set(getNewKey(key.toString()), Boolean.parseBoolean(value.toString()));
                    break;
                case "ERROR_MILLIS":
                    yaml.set(getNewKey(key.toString()), Long.parseLong(value.toString()));
            }
        }

        // Save the yaml
        yaml.save(new File(file.getParentFile(), "config.yml"));
        // Send info
        ZLogger.info("Successfully loaded data from old config.");
    }

    private static String getDefaultConfig() {
        // Catch errors
        try {
            // Get input stream
            InputStream stream = ZLevels.class.getResourceAsStream("/config.yml");
            // Return string
            return AutoRole.fromStream(stream);
        } catch (Exception e) {
            // Send warning
            ZLogger.warn("Could not read template config!");
            // Return null
            return null;
        }
    }

    private static String getNewKey(String oldKey) {
        // Switch old key
        switch (oldKey) {
            case "NAME":
                return "basic-settings.name";
            case "TOKEN":
                return "basic-settings.token";
            case "PREFIX":
                return "basic-settings.prefix";
            case "SERVER":
                return "basic-settings.server";
            case "EMBED_COLOUR":
                return "basic-settings.embed-colour";
            case "GAME_STATUS":
                return "basic-settings.game-status";
            case "OPS":
                return "basic-settings.ops";
            case "DB_HOST":
                return "database.host";
            case "DB_PORT":
                return "database.port";
            case "DB_NAME":
                return "database.name";
            case "DB_USER":
                return "database.user";
            case "DB_PASS":
                return "database.pass";
            case "ERROR_MILLIS":
                return "more-settings.error-length";
            case "AUTOROLE_ENABLED":
                return "more-settings.autorole-enabled";
            case "RATINGS_ENABLED":
                return "staff-ratings.enabled";
            case "STAFF_ROLE":
                return "staff-ratings.staff-role";
            case "CHANNELS_FOR_RATINGS":
                return "staff-ratings.channels";
            default:
                return null;
        }
    }

}
