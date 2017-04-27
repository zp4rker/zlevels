package me.zp4rker.dscrd.zlevels;

import me.zp4rker.dscrd.core.cmd.handler.CommandHandler;
import me.zp4rker.dscrd.zlevels.core.config.Config;
import me.zp4rker.dscrd.zlevels.core.db.Database;
import me.zp4rker.dscrd.core.logger.ZLogger;
import me.zp4rker.dscrd.zlevels.listeners.*;
import me.zp4rker.dscrd.zlevels.core.util.AutoRole;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ZP4RKER
 */
public class ZLevels {

    public static CommandHandler handler;
    public static JDA jda;

    public static ExecutorService async = Executors.newCachedThreadPool();

    public static final String VERSION = "v1.0.8";

    public static void main(String[] args) {
        // Add blank line
        ZLogger.blankLine();
        // Send info
        ZLogger.info("Starting ZLevels...");
        // Check if config is valid
        if (!Config.load()) {
            // Send warning
            ZLogger.warn("Config was invalid or missing! Stopping ZLevels...");
            // Return
            return;
        }
        // Check if roles file is valid
        if (!AutoRole.load()) {
            // Send warning
            ZLogger.warn("Roles file was invalid or missing! Stopping ZLevels...");
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
                    .addEventListener(new ReadyListener()) // Ready listeners
                    .addEventListener(handler) // Command handler
                    .addEventListener(new MessageSendListener()) // Message send listeners
                    .addEventListener(new MemberLeaveListener()) // Member leave listeners
                    .addEventListener(new ReactionAddListener()) // Reaction add listeners
                    .addEventListener(new ReactionRemoveListener()) // Reaction remove listner
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
