package me.zp4rker.zlevels.util;

import me.zp4rker.zlevels.ZLevels;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.UserData;
import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.core.yaml.ConfigurationSection;
import me.zp4rker.core.yaml.file.Yaml;
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
        if (!Config.AUTOROLE_ENABLED) return true;

        try {
            File file = new File(ZLevels.getDirectory(), "roles.yml");
            if (!file.exists()) {
                ZLogger.info("No roles.yml exists, creating default file...");

                file.createNewFile();
                FileWriter writer = new FileWriter(file);

                writer.write(getDefaultFile());
                writer.flush();
                writer.close();
            }

            Yaml data = Yaml.loadConfiguration(file);
            for (String key : data.getKeys(false)) {
                roles.put(data.getInt(key + ".level"), data.getConfigurationSection(key));
            }
            ZLogger.info("Successfully loaded auto-assign data for " + roles.size() + " roles!");

            return true;
        } catch (Exception e) {
            ZLogger.warn("Could not load data from roles.yml");

            return false;
        }
    }

    public static void assignRole(UserData data) {
        User user = ZLevels.jda.getUserById(data.getUserId());
        Guild guild = ZLevels.jda.getGuildById(Config.SERVER);
        int level = data.getLevel();

        for (int requiredLevel : roles.keySet()) {
            if (level < requiredLevel) return;

            Member member = guild.getMember(user);
            Role role = guild.getRolesByName(roles.get(requiredLevel).getString("name"), false).get(0);

            if (member == null || role == null) return;
            if (member.getRoles().contains(role)) return;

            try {
                guild.getController().addRolesToMember(member, role).queue();
            } catch (Exception e) {
                ZLogger.warn("Could not assign role to " + user.getName() + "!");
            }
        }
    }

    private static String getDefaultFile() {
        try {
            InputStream stream = ZLevels.class.getResourceAsStream("/roles.yml");

            return fromStream(stream);
        } catch (Exception e) {
            ZLogger.warn("Could not read template file!");

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
