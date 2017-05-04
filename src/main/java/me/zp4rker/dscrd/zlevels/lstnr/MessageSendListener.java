package me.zp4rker.dscrd.zlevels.lstnr;

import me.zp4rker.dscrd.zlevels.ZLevels;
import me.zp4rker.dscrd.zlevels.config.Config;
import me.zp4rker.dscrd.zlevels.db.UserData;
import me.zp4rker.dscrd.zlevels.util.LevelsUtil;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author ZP4RKER
 */
public class MessageSendListener {

    private static final List<String> spamFilter = new ArrayList<>();

    @SubscribeEvent
    public void onMessage(MessageReceivedEvent event) {
        // Check if in server
        if (event.getGuild() == null) return;
        // Check if correct server
        if (!event.getGuild().getId().equals(Config.SERVER)) return;
        // Check if bot
        if (event.getAuthor().isBot()) return;
        // Check spam filter
        if (!spamFilter.contains(event.getAuthor().getId())) {
            // Check if command
            if (event.getMessage().getContent().startsWith("-")) return;
            // Run asynchronously
            ZLevels.async.submit(() -> {
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
