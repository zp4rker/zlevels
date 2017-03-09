package com.zp4rker.zlevels.listener;

import com.zp4rker.zlevels.db.UserData;
import com.zp4rker.zlevels.util.Config;
import com.zp4rker.zlevels.util.LevelsUtil;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

/**
 * @author ZP4RKER
 */
public class MessageSendListener {

    private static final List<String> spamFilter = new ArrayList<>();

    @SubscribeEvent
    public void onMessage(MessageReceivedEvent event) {
        // Check if correct server
        if (!event.getMessage().getGuild().getId().equals(Config.SERVER)) return;
        // Check if bot
        if (event.getAuthor().isBot()) return;
        // Check spam filter
        if (!spamFilter.contains(event.getAuthor().getId())) {
            // Check if command
            if (event.getMessage().getContent().startsWith("-")) return;
            // Run asynchronously
            Executors.newSingleThreadExecutor().submit(() -> {
                // Get data
                UserData data = UserData.fromId(event.getAuthor().getId());
                // Check if exists
                if (data == null) {
                    // Create new data
                    data = new UserData();
                    // Set user id
                    data.setUserId(event.getAuthor().getId());
                }
                // Get random xp
                long randomXp = LevelsUtil.randomXp(10, 25);
                // Add xp
                data.setTotalXp(data.getTotalXp() + randomXp);
                // Save data
                data.save();
                // Add to list
                spamFilter.add(event.getAuthor().getId());
                // Start timer
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Remove from list
                        spamFilter.remove(event.getAuthor().getId());
                    }
                }, 1000 * 60);
            });
        }
    }

}
