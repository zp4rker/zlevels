package com.zp4rker.zlevels.commands;

import com.zp4rker.zlevels.cmd.CommandExecutor;
import com.zp4rker.zlevels.cmd.RegisterCommand;
import com.zp4rker.zlevels.db.UserData;
import com.zp4rker.zlevels.util.AutoRole;
import com.zp4rker.zlevels.util.Config;
import com.zp4rker.zlevels.util.Pruner;
import com.zp4rker.zlevels.util.ZLogger;
import net.dv8tion.jda.core.entities.Message;

import java.util.concurrent.Executors;

/**
 * @author ZP4RKER
 */
public class FlushCommand implements CommandExecutor {

    @RegisterCommand(aliases = "flush")
    public String onCommand(Message message) {
        // Run asynchronously
        Executors.newSingleThreadExecutor().submit(() -> {
            // Check if sent by ZP4RKER
            if (!message.getAuthor().getDiscriminator().equals("5928")) return;
            // Prune members
            Pruner.prune();
            // Force auto role
            forceAutoRole();
        });
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

}
