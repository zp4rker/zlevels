package me.zp4rker.zlevels.util;

import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.zlevels.ZLevels;
import me.zp4rker.zlevels.config.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author ZP4RKER
 */
public class MessageUtil {

    public static void sendError(String error, String errorMessage, Message message) {
        ZLevels.async.submit(() -> {
            try {
                Message futureMessage = message.getChannel().sendMessage(new EmbedBuilder().setFooter(error, null)
                        .setDescription(errorMessage).setColor(Color.RED).build()).complete();

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        message.getTextChannel().deleteMessages(Arrays.asList(message, futureMessage)).complete();
                    }
                }, Config.ERROR_LENGTH);
            } catch (Exception e) {
                ZLogger.info("Could not send error message!");
            }
        });
    }

}
