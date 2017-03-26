package com.zp4rker.dscrd.zlevels.listeners;

import com.zp4rker.dscrd.zlevels.commands.*;
import com.zp4rker.dscrd.zlevels.core.config.Config;
import com.zp4rker.dscrd.zlevels.ZLevels;
import com.zp4rker.dscrd.zlevels.core.db.Database;
import com.zp4rker.dscrd.zlevels.core.db.StaffRating;
import com.zp4rker.dscrd.core.logger.ZLogger;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.concurrent.Executors;

/**
 * @author ZP4RKER
 */
public class ReadyListener {

    @SubscribeEvent
    public void onReady(ReadyEvent event) {
        // Send info
        ZLogger.info("Registering comands...");
        // Register help command
        ZLevels.handler.registerCommand(new HelpCommand());
        // Register rank command
        ZLevels.handler.registerCommand(new RankCommand());
        // Register leaderboard command
        ZLevels.handler.registerCommand(new TopCommand());
        // Register rating command
        ZLevels.handler.registerCommand(new RatingCommand());
        // Register rewards command
        ZLevels.handler.registerCommand(new RewardsCommand());
        // Register inactive command
        ZLevels.handler.registerCommand(new InactiveCommand());
        // Register flush command
        ZLevels.handler.registerCommand(new FlushCommand());
        // Send info
        ZLogger.info("Successfully registered " + ZLevels.handler.getCommands().values().size() + " commands!");
        // Check if game status not empty
        if (!Config.GAME_STATUS.isEmpty()) {
            // Send info
            ZLogger.info("Setting game status...");
            // Set game status
            event.getJDA().getPresence().setGame(new Game() {
                @Override
                public String getName() {
                    return Config.GAME_STATUS;
                }

                @Override
                public String getUrl() {
                    return null;
                }

                @Override
                public GameType getType() {
                    return GameType.DEFAULT;
                }
            });
        }
        // Run asynchonously
        Executors.newSingleThreadExecutor().submit(() -> {
            // Load DB
            Database.load();
            // Start StaffRating month
            StaffRating.startMonth();
            // Send info
            ZLogger.info("ZLevels " + ZLevels.VERSION + " is ready!");
        });
    }

}