package me.zp4rker.zlevels;

import me.zp4rker.core.command.handler.CommandHandler;
import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.zlevels.cmd.StopCommand;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.Database;
import me.zp4rker.zlevels.lstnr.*;
import me.zp4rker.zlevels.util.AutoRole;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main class of the ZLevels bot.
 *
 * @author ZP4RKER
 */
public class ZLevels {

    public static CommandHandler handler;
    public static JDA jda;

    public static ExecutorService async = Executors.newCachedThreadPool();

    public static final String VERSION = "v1.0.9";

    /**
     * The method to run on startup of the java application.
     *
     * @param args The runtime arguments.
     */
    public static void main(String[] args) {
        ZLogger.initialise();

        ZLogger.blankLine();

        ZLogger.info("Starting ZLevels...");

        if (!Config.load()) {
            ZLogger.warn("Config was invalid or missing! Stopping ZLevels...");

            return;
        }


        if (!AutoRole.load()) {
            ZLogger.warn("Roles file was invalid or missing! Stopping ZLevels...");
        }

        if (!Database.canConnect()) {
            ZLogger.warn("Could not establish a connection with the db! Stopping ZLevels...");

            return;
        }

        try {
            handler = new CommandHandler(Config.PREFIX);

            jda = new JDABuilder(AccountType.BOT)
                    .setToken(Config.TOKEN)
                    .setEventManager(new AnnotatedEventManager())
                    .addEventListener(new ReadyListener())
                    .addEventListener(handler)
                    .addEventListener(new MessageSendListener())
                    .addEventListener(new MemberLeaveListener())
                    .addEventListener(new ReactionAddListener())
                    .addEventListener(new ReactionRemoveListener())
                    .buildAsync();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    StopCommand.shutdown(jda);
                }
            });
        } catch (Exception e) {
            ZLogger.warn("Could not connect: Invalid token!");
        }
    }

    /**
     * Get the directory where all the files for ZLevels are located.
     *
     * @return The directory.
     */
    public static File getDirectory() {
        return new File(ZLevels.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
    }

}
