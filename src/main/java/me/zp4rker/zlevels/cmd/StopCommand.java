package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.ICommand;
import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.UserData;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;

/**
 * @author ZP4RKER
 */
public class StopCommand implements ICommand {

    @RegisterCommand(aliases = "stop",
                    usage = "{prefix}stop",
                    description = "Stops the bot.")
    public void onCommand(Message message, String[] args) {
        String userId = message.getAuthor().getId();
        if (Config.OPS.stream().noneMatch(s -> s.equals(userId))) return;

        if (args.length > 0) {
            if (Arrays.stream(args).noneMatch((arg) -> arg.equalsIgnoreCase("levels"))) return;
        }

        message.getGuild().getTextChannelById(Config.LOG_CHANNEL).sendMessage("Stopping ZLevels...").complete();

        shutdown(message.getJDA());
    }

    private void shutdown(JDA jda) {
        UserData.flushCache();

        ZLogger.info("Stopping ZLevels...");

        jda.shutdown();
        System.exit(0);
    }

}
