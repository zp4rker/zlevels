package com.zp4rker.zlevels;

import com.zp4rker.zlevels.cmd.handler.CommandHandler;
import com.zp4rker.zlevels.db.Database;
import com.zp4rker.zlevels.listener.*;
import com.zp4rker.zlevels.util.Config;
import com.zp4rker.zlevels.util.ZLogger;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import java.io.File;

/**
 * @author ZP4RKER
 */
public class ZLevels {

    public static CommandHandler handler;
    public static JDA jda;

    public static final String VERSION = "v1.1";

    public static void main(String[] args) {
        // Add blank line
        System.out.println();
        // Send info
        ZLogger.info("Starting ZLevels...");
        // Check if config is valid
        if (!Config.load()) {
            // Send warning
            ZLogger.warn("Config was invalid or missing! Stopping ZLevels...");
            // Return
            return;
        }
        // Check if can connect
        if (!Database.canConnect()) {
            // Send warning
            ZLogger.warn("Could not establish a connection with the database! Stopping ZLevels...");
            // Return
            return;
        }
        try {
            // Create command handler
            handler = new CommandHandler(Config.PREFIX);
            // Get JDA
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(Config.TOKEN)
                    .setEventManager(new AnnotatedEventManager()) // Use Annotation event manager
                    .addListener(new ReadyListener()) // Ready listener
                    .addListener(handler) // Command handler
                    .addListener(new MessageSendListener()) // Message send listener
                    .addListener(new MemberLeaveListener()) // Member leave listener
                    .addListener(new ReactionAddListener()) // Reaction add listener
                    .addListener(new ReactionRemoveListener()) // Reaction remove listner
                    .buildBlocking();
        } catch (Exception e) {
            // Send error
            ZLogger.warn("Could not connect: Invalid token!");
        }
    }

    public static File getDirectory() {
        // Return directory
        return new File(ZLevels.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
    }

}
