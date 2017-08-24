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

    private static Message futureMessage;

    public static Message sendError(String error, String errorMessage, Message message) {
        futureMessage = null;
        ZLevels.async.submit(() -> {
            try {
                futureMessage = message.getChannel().sendMessage(new EmbedBuilder().setFooter(error, null)
                        .setDescription(errorMessage).setColor(Color.RED).build()).complete();
            } catch (Exception e) {
                ZLogger.info("Could not send error message!");
            }
        });

        return futureMessage;
    }

    public static void selfDestruct(Message message, long delay) {
        if (message == null) return;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                message.delete().complete();
            }
        }, delay);
    }

    public static void bypassDeleteLogs(Message... messages) {
        messages[0].getTextChannel().deleteMessages(Arrays.asList(messages)).complete();
    }

}
