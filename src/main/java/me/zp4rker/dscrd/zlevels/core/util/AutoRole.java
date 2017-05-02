package me.zp4rker.dscrd.zlevels.core.util;

import me.zp4rker.dscrd.zlevels.ZLevels;
import me.zp4rker.dscrd.zlevels.core.config.Config;
import me.zp4rker.dscrd.zlevels.core.db.UserData;
import me.zp4rker.dscrd.core.logger.ZLogger;
import me.zp4rker.dscrd.core.yaml.ConfigurationSection;
import me.zp4rker.dscrd.core.yaml.file.Yaml;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.io.*;
import java.util.HashMap;

/**
 * @author ZP4RKER
 */
public class AutoRole {

    public static final HashMap<Integer, ConfigurationSection> roles = new HashMap<>();

    public static boolean load() {
        // Check if enabled
        if (!Config.AUTOROLE_ENABLED) return true;
        // Catch errors
        try {
            // Get file
            File file = new File(ZLevels.getDirectory(), "roles.yml");
            // Check if file exists
            if (!file.exists()) {
                // Send info
                ZLogger.info("No roles.yml exists, creating default file...");
                // Create the file
                file.createNewFile();
                // Get file writer
                FileWriter writer = new FileWriter(file);
                // Write default file
                writer.write(getDefaultFile());
                // Flush
                writer.flush();
                // Close
                writer.close();
            }
            // Get data
            Yaml data = Yaml.loadConfiguration(file);
            // Loop through
            for (String key : data.getKeys(false)) {
                // Add to hashmap
                roles.put(data.getInt(key + ".level"), data.getConfigurationSection(key));
            }
            // Send info
            ZLogger.info("Successfully loaded auto-assign data for " + roles.size() + " roles!");
            // Return true
            return true;
        } catch (Exception e) {
            // Send warning
            ZLogger.warn("Could not load data from roles.yml");
            // Return false
            return false;
        }
    }

    public static void assignRole(UserData data) {
        // Get user
        User user = ZLevels.jda.getUserById(data.getUserId());
        // Get guild
        Guild guild = ZLevels.jda.getGuildById(Config.SERVER);
        // Get level
        int level = data.getLevel();
        // Loop through roles
        for (int requiredLevel : roles.keySet()) {
            // Check if high enough
            if (level < requiredLevel) return;
            // Get member
            Member member = guild.getMember(user);
            // Get role
            Role role = guild.getRolesByName(roles.get(requiredLevel).getString("name"), false).get(0);
            // Check if exists
            if (member == null || role == null) return;
            // Check if has role
            if (member.getRoles().contains(role)) return;
            // Catch errors
            try {
                // Assign role
                guild.getController().addRolesToMember(member, role).queue();
            } catch (Exception e) {
                // Send warning
                ZLogger.warn("Could not assign role to " + user.getName() + "!");
            }
        }
    }

    private static String getDefaultFile() {
        // Catch errors
        try {
            // Get input stream
            InputStream stream = ZLevels.class.getResourceAsStream("/roles.yml");
            // Return string
            return fromStream(stream);
        } catch (Exception e) {
            // Send warning
            ZLogger.warn("Could not read template file!");
            // Return null
            return null;
        }
    }

    public static String fromStream(InputStream stream) throws Exception {
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
