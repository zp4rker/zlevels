package com.zp4rker.zlevels.util;

import com.zp4rker.zlevels.ZLevels;
import com.zp4rker.zlevels.db.UserData;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 * @author ZP4RKER
 */
public class AutoRole {

    public static final HashMap<Integer, JSONObject> roles = new HashMap<>();

    public static void load() {
        // Check if enabled
        if (!Config.AUTOROLE_ENABLED) return;
        try {
            // Get file
            File file = new File(ZLevels.getDirectory(), "roles.json");
            // Get file reader
            FileReader reader = new FileReader(file);
            // Get data as JSON
            JSONObject data = (JSONObject) new JSONParser().parse(reader);
            // Loop through
            for (Object object : data.values()) {
                // Get as JSONObject
                JSONObject role = (JSONObject) object;
                // Add to hashmap
                roles.put(Integer.valueOf(role.get("level").toString()), role);
            }
        } catch (Exception e) {
            // Send warning
            ZLogger.warn("Could not load data from roles.json");
            e.printStackTrace();
        }
        // Send info
        ZLogger.info("Successfully loaded auto-assign data for " + roles.size() + " roles!");
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
            Role role = guild.getRolesByName(roles.get(requiredLevel).get("name").toString(), false).get(0);
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

}
