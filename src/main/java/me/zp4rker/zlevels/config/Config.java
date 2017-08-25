package me.zp4rker.zlevels.config;

import me.zp4rker.core.yaml.file.Yaml;
import me.zp4rker.zlevels.ZLevels;
import me.zp4rker.zlevels.util.AutoRole;
import me.zp4rker.core.logger.ZLogger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The collection of all configurations.
 *
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
    public static String LOG_CHANNEL = "";

    public static String DB_HOST = "";
    public static String DB_PORT = "";
    public static String DB_NAME = "";
    public static String DB_USER = "";
    public static String DB_PASS = "";

    public static long ERROR_LENGTH = 6000;

    public static boolean AUTOROLE_ENABLED = true;

    /**
     * Loads the config file.
     *
     * @return Whether or not it could load the configurations from the config file.
     */
    public static boolean load() {
        try {
            File file = new File(ZLevels.getDirectory(), "config.yml");
            if (!file.exists()) {
                if (new File(file.getParentFile(), "config.json").exists()) {
                    ZLogger.info("Old config exists, getting data from old config...");

                    transferFromJSON();

                    ZLogger.info("Now deleting old config...");

                    new File(file.getParentFile(), "config.json").delete();

                    ZLogger.info("Successfully deleted old config.");
                } else {
                    ZLogger.info("No config exists, creating default config...");

                    file.createNewFile();
                    FileWriter writer =  new FileWriter(file);
                    writer.write(getDefaultConfig());
                    writer.flush();
                    writer.close();

                    return false;
                }
            }

            readConfig();

            return true;
        } catch (ConfigException e) {
            ZLogger.warn(e.getCause().getMessage() + " can not be empty!");

            return false;
        } catch (Exception e) {
            ZLogger.warn("Could not load config!");

            return false;
        }
    }

    /**
     * Reads the values from the config and applies them to the fields.
     *
     * @throws Exception When something wrong is found in the config file.
     */
    private static void readConfig() throws Exception {
        File file = new File(ZLevels.getDirectory(), "config.yml");

        Yaml data = Yaml.loadConfiguration(file);

        for (Field field : Config.class.getDeclaredFields()) {
            String key = getNewKey(field.getName());

            if (key == null) continue;

            if (data.getString(key) == null || data.getString(key).isEmpty()) {

                if (data.getString(key) != null && key.equals("basic-settings.game-status")) continue;

                if (key.equals("staff-ratings.staff-role") || key.equals("staff-ratings.staff-role")) {

                    if (!data.getBoolean("staff-ratings.enabled")) continue;

                }

                data.set(key, "");
                data.save(file);

                throw new ConfigException(new Throwable(key));
            }
        }

        NAME = data.getString("basic-settings.name");
        TOKEN = data.getString("basic-settings.token");
        PREFIX = data.getString("basic-settings.prefix");
        SERVER = data.getString("basic-settings.server");
        OPS = data.getStringList("basic-settings.ops");
        EMBED_COLOUR = data.getString("basic-settings.embed-colour");
        GAME_STATUS = data.getString("basic-settings.game-status");
        LOG_CHANNEL = data.getString("basic-settings.log-channel");

        DB_HOST = data.getString("database.host");
        DB_PORT = data.getString("database.port");
        DB_NAME = data.getString("database.name");
        DB_USER = data.getString("database.user");
        DB_PASS = data.getString("database.pass");

        ERROR_LENGTH = data.getLong("more-settings.error-length");
        AUTOROLE_ENABLED = data.getBoolean("more-settings.autorole-enabled");

        ZLogger.info("Successfully loaded settings from config.");
    }

    /**
     * Transfers data from the old config type: JSON.
     *
     * @throws Exception If file config.json cannot be found.
     */
    private static void transferFromJSON() throws Exception {
        File file = new File(ZLevels.getDirectory(), "config.json");
        FileReader reader = new FileReader(file);

        JSONObject data = (JSONObject) new JSONParser().parse(reader);
        Yaml yaml = Yaml.loadConfiguration(new InputStreamReader(ZLevels.class.getResourceAsStream("/config.yml")));

        for (Object key : data.keySet()) {
            Object value = data.get(key.toString());

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

        yaml.save(new File(file.getParentFile(), "config.yml"));

        ZLogger.info("Successfully loaded data from old config.");
    }

    /**
     * Gets the default config from the file embedded in the jar.
     *
     * @return A string of the default config's contents.
     */
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

    /**
     * Gets the key of the new config type: YAML from the key of the old config type: JSON.
     *
     * @param oldKey The key in the JSON config.
     * @return The key in the YAML config.
     */
    private static String getNewKey(String oldKey) {
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
            case "LOG_CHANNEL":
                return "basic-settings.log-channel";
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
            default:
                return null;
        }
    }

}
