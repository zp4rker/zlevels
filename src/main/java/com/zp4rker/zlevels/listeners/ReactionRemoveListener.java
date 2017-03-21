package com.zp4rker.zlevels.listeners;

import com.zp4rker.zlevels.core.db.StaffRating;
import com.zp4rker.zlevels.core.config.Config;
import com.zp4rker.zlevels.core.util.ZLogger;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.Arrays;
import java.util.concurrent.Executors;

/**
 * @author ZP4RKER
 */
public class ReactionRemoveListener {

    @SubscribeEvent
    public void onReactionRemove(MessageReactionRemoveEvent event) {
        // Run asynchronously
        Executors.newSingleThreadExecutor().submit(() -> {
            // Check if ratings enabled
            if (!Config.RATINGS_ENABLED) return;
            // Catch errors
            try {
                // Get message
                Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
                // Check if correct server
                if (!message.getGuild().getId().equals(Config.SERVER)) return;
                // Check if staff
                if (message.getGuild().getMember(message.getAuthor()).getRoles().stream().noneMatch(role -> role
                        .getName().equals(Config.STAFF_ROLE))) return;
                // Check channels
                if (!Arrays.asList(Config.CHANNELS_FOR_RATINGS).contains(event.getChannel().getId())) return;
                // Get rating
                StaffRating rating = StaffRating.fromId(message.getAuthor().getId());
                // Check if exists
                if (rating == null) return;
                // Check if ratings is 0
                if (rating.getRatings() > 0) {
                    // Decrement ratings
                    rating.setRatings(rating.getRatings() - 1);
                    // Check if monthly ratings is 0
                    if (rating.getMonthlyRatings() > 0) {
                        // Decrement monthly ratings
                        rating.setMonthlyRatings(rating.getMonthlyRatings() - 1);
                    }
                }
                // Save rating
                rating.save();
            } catch (Exception e) {
                // Send warning
                ZLogger.warn("Could not handle ReactionRemoveEvent correctly!");
            }
        });
    }

}
