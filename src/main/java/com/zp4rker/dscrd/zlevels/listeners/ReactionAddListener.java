package com.zp4rker.dscrd.zlevels.listeners;

import com.zp4rker.dscrd.zlevels.core.db.StaffRating;
import com.zp4rker.dscrd.zlevels.core.db.UserData;
import com.zp4rker.dscrd.zlevels.core.config.Config;
import com.zp4rker.dscrd.core.logger.ZLogger;
import com.zp4rker.dscrd.zlevels.core.util.LevelsUtil;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.*;
import java.util.concurrent.Executors;

/**
 * @author ZP4RKER
 */
public class ReactionAddListener {

    private static final List<String> spamFilter = new ArrayList<>();

    @SubscribeEvent
    public void onReaction(MessageReactionAddEvent event) {
        // Run asynchronously
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                // Check if in server
                if (!event.getChannel().getType().equals(ChannelType.TEXT)) return;
                // Get message
                Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
                // Check if correct server
                if (!message.getGuild().getId().equals(Config.SERVER)) return;
                // Check if bot
                if (event.getChannel().getMessageById(event.getMessageId()).complete().getAuthor().isBot()) return;
                // Check if self
                if (event.getChannel().getMessageById(event.getMessageId()).complete().getAuthor().equals(event.getUser())) return;
                // Check spam filter
                if (spamFilter.contains(event.getUser().getId())) return;
                // Check if already added reaction
                if (alreadyReacted(message, event)) return;
                // Get data
                UserData data = UserData.fromId(message.getAuthor().getId());
                // Check if exists
                if (data == null) {
                    // Create new data
                    data = new UserData();
                    // Set user id
                    data.setUserId(message.getAuthor().getId());
                }
                // Get random xp
                long randomXp = LevelsUtil.randomXp(15, 25);
                // Add xp
                data.setTotalXp(data.getTotalXp() + randomXp);
                // Save data
                data.save();
                // Add to spam filter
                spamFilter.add(event.getUser().getId());
                // Start timer
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Remove from spam filter
                        spamFilter.remove(event.getUser().getId());
                    }
                }, 1000 * 60);
            } catch (Exception e) {
                // Send warning
                ZLogger.warn("Could not handle ReactionAddEvent correctly!");
            }
        });
        // Run asynchronously
        Executors.newSingleThreadExecutor().submit(() -> {
            // Check if ratings enabled
            if (!Config.RATINGS_ENABLED) return;
            // Catch errors
            try {
                // Check if in server
                if (!event.getChannel().getType().equals(ChannelType.TEXT)) return;
                // Check channels
                if (!Config.CHANNELS_FOR_RATINGS.contains(event.getChannel().getId())) return;
                // Check if bot
                if (event.getChannel().getMessageById(event.getMessageId()).complete().getAuthor().isBot()) return;
                // Check if self
                if (event.getChannel().getMessageById(event.getMessageId()).complete().getAuthor().equals(event.getUser())) return;
                // Get message
                Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
                // Check if already added reaction
                if (alreadyReacted(message, event)) return;
                // Get Guild
                Guild guild = ((TextChannel) event.getChannel()).getGuild();
                // Get user
                User user = message.getAuthor();
                // Check if staff
                if (guild.getMember(user).getRoles().stream()
                        .noneMatch(role -> role.getName().equals(Config.STAFF_ROLE))) return;
                // Get staff rating
                StaffRating rating = StaffRating.fromId(user.getId());
                // Check if exists
                if (rating == null) {
                    // Create new staff rating
                    rating = new StaffRating();
                    // Set user id
                    rating.setUserId(user.getId());
                }
                // Add to rating
                rating.setRatings(rating.getRatings() + 1);
                // Add to monthly rating
                rating.setMonthlyRatings(rating.getMonthlyRatings() + 1);
                // Save rating
                rating.save();
            } catch (Exception e) {
                // Send warning
                ZLogger.warn("Could not handle ReactionAddEvent correctly!");
            }
        });
    }

    private boolean alreadyReacted(Message message, MessageReactionAddEvent event) {
        // Loop through all reactions
        return message.getReactions().stream().anyMatch(reaction -> {
            try {
                // Check if this reaction
                if (reaction.equals(event.getReaction())) return false;
                // Return if contains user
                return reaction.getUsers().complete().contains(event.getUser());
            } catch (Exception e) {
                // Return false
                return false;
            }
        });
    }

}
