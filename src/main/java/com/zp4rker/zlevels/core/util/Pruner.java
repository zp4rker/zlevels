package com.zp4rker.zlevels.core.util;

import com.zp4rker.zlevels.ZLevels;
import com.zp4rker.zlevels.core.config.Config;
import com.zp4rker.zlevels.core.db.UserData;
import net.dv8tion.jda.core.entities.Guild;

/**
 * @author ZP4RKER
 */
public class Pruner {

    public static void prune() {
        // Catch errors
        try {
            // Send info
            ZLogger.info("Formulating members to prune...");
            // Get server
            Guild server = ZLevels.jda.getGuildById(Config.SERVER);
            // Start count
            int i = 0;
            // Loop through all user data
            for (UserData userData : UserData.getAllData()) {
                // Check if in server
                if (server.getMembers().stream().anyMatch(member -> {
                    // Return if same id
                    return userData.getUserId().equals(member.getUser().getId());
                })) continue;
                // Delete userdata
                userData.delete();
                // Increment count
                i++;
            }
            // Send info
            ZLogger.info("Successfully pruned data for " + i + " users.");
        } catch (Exception e) {
            e.printStackTrace();
            // Send warning
            ZLogger.info("Could not prune members!");
        }
    }

}
