package com.zp4rker.dscrd.zlevels.commands;

import com.zp4rker.dscrd.core.cmd.CommandExecutor;
import com.zp4rker.dscrd.core.cmd.RegisterCommand;
import com.zp4rker.dscrd.zlevels.core.config.Config;
import com.zp4rker.dscrd.zlevels.core.db.UserData;
import com.zp4rker.dscrd.core.logger.ZLogger;
import com.zp4rker.dscrd.zlevels.core.util.AutoRole;
import com.zp4rker.dscrd.zlevels.core.util.Pruner;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ZP4RKER
 */
public class FlushCommand implements CommandExecutor {

    @RegisterCommand(aliases = "flush")
    public String onCommand(Message message) {
        // Get id
        String id = message.getAuthor().getId();
        // Check if sent by OP
        if (Config.OPS.stream().noneMatch(s -> s.equals(id))) return null;
        // Prune members
        Pruner.prune();
        // Force auto role
        forceAutoRole();
        // Save all data
        saveAllData();
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
        // Send info
        ZLogger.info("Successfully saved all user data.");
    }

}
