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
    public void onCommand(Message message) {
        String id = message.getAuthor().getId();

        if (Config.OPS.stream().noneMatch(s -> s.equals(id))) return;

        forceAutoRole();
        saveAllData();
        Pruner.prune();
    }

    private void forceAutoRole() {
        if (Config.AUTOROLE_ENABLED) {
            ZLogger.info("Forcing auto role on all user data...");

            for (UserData data : UserData.getAllData()) {
                AutoRole.assignRole(data);
            }

            ZLogger.info("Successfully forced auto role on all user data.");
        }
    }

    private void saveAllData() {
        ZLogger.info("Saving all user data...");

        for (UserData data : UserData.getAllData()) {
            data.save();
        }

        UserData.flushCache();

        ZLogger.info("Successfully saved all user data.");
    }

}
