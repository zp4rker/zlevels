package me.zp4rker.zlevels.util;

import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.zlevels.ZLevels;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.UserData;
import net.dv8tion.jda.core.entities.Guild;

/**
 * @author ZP4RKER
 */
public class Pruner {

    public static void prune() {
        try {
            ZLogger.info("Formulating members to prune...");

            Guild server = ZLevels.jda.getGuildById(Config.SERVER);

            int i = 0;

            for (UserData userData : UserData.getAllData()) {
                if (server.getMembers().stream().anyMatch(member ->
                        userData.getUserId().equals(member.getUser().getId()))) continue;
                userData.delete();
                i++;
            }

            ZLogger.info("Successfully pruned data for " + i + " users.");
        } catch (Exception e) {
            e.printStackTrace();
            ZLogger.info("Could not prune members!");
        }
    }

}
