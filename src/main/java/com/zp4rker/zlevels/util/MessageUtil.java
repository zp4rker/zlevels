package com.zp4rker.zlevels.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

/**
 * @author ZP4RKER
 */
public class MessageUtil {

    public static void sendError(String error, String errorMessage, Message message) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                // Send the embed
                Message futureMessage = message.getChannel().sendMessage(new EmbedBuilder().setFooter(error, null)
                        .setTitle(errorMessage, null).setColor(Color.RED).build()).complete();
                // Start timer
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Delete the future message
                        futureMessage.delete().queue();
                        // Delete the message
                        message.delete().queue();
                    }
                }, Config.ERROR_MILLIS);
            } catch (Exception e) {
                // Send warning
                ZLogger.info("Could not send error message!");
            }
        });
    }

}
