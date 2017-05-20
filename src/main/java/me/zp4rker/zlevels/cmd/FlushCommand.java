package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.core.command.ICommand;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.UserData;
import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.zlevels.util.AutoRole;
import me.zp4rker.zlevels.util.LevelsUtil;
import me.zp4rker.zlevels.util.Pruner;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ZP4RKER
 */
public class FlushCommand implements ICommand {

    @RegisterCommand(aliases = "flush",
                    usage = "{prefix}flush",
                    description = "Prunes members, forces auto-role and saves all data.")
    public String onCommand(Message message) {
        // Get id
        String id = message.getAuthor().getId();
        // Check if sent by OP
        if (Config.OPS.stream().noneMatch(s -> s.equals(id))) return null;
        // Force auto role
        forceAutoRole();
        // Save all data
        saveAllData();
        // Prune data
        Pruner.prune();
        // Loop through first page
        for (UserData data : LevelsUtil.getPage(0)) {
            // Send debug
            ZLogger.debug("data: " + data.getUsername());
        }
        // Loop through fifth page
        for (UserData data : LevelsUtil.getPage(4)) {
            // Send debug
            ZLogger.debug("data: " + data.getUsername());
        }
        // Return null
        return null;
    }

    private void forceAutoRole() {
        // Check if autorole enabled
        if (Config.AUTOROLE_ENABLED) {
            // Send info
            ZLogger.info("Forcing auto role on all user data...");
            // Loop through all user data
            for (UserData data : UserData.getAllData()) {
                // Assign role for data
                AutoRole.assignRole(data);
            }
            // Send info
            ZLogger.info("Successfully forced auto role on all user data.");
        }
    }

    private void saveAllData() {
        // Send info
        ZLogger.info("Saving all user data...");
        // Loop through all data
        for (UserData data : UserData.getAllData()) {
            // Save data
            data.save();
        }
        UserData.flushCache();
        // Send info
        ZLogger.info("Successfully saved all user data.");
    }

}
