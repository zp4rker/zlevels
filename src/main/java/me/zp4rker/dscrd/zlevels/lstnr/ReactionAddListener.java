package me.zp4rker.dscrd.zlevels.lstnr;

import me.zp4rker.dscrd.zlevels.ZLevels;
import me.zp4rker.dscrd.zlevels.cmd.LeaderboardCommand;
import me.zp4rker.dscrd.zlevels.db.StaffRating;
import me.zp4rker.dscrd.zlevels.db.UserData;
import me.zp4rker.dscrd.zlevels.config.Config;
import me.zp4rker.dscrd.core.logger.ZLogger;
import me.zp4rker.dscrd.zlevels.util.LevelsUtil;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.*;

/**
 * @author ZP4RKER
 */
public class ReactionAddListener {

    private static final List<String> spamFilter = new ArrayList<>();

    @SubscribeEvent
    public void onReaction(MessageReactionAddEvent event) {
        // Add XP
        ZLevels.async.submit(() -> {
            // Catch errors
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
        // Add staff rating
        ZLevels.async.submit(() -> {
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
        // Leaderboard controls
        ZLevels.async.submit(() -> {
            // Get message
            Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
            // Check if self
            if (event.getUser().getId().equals(event.getJDA().getSelfUser().getId())) return;
            // Check if message is embed
            if (message.getEmbeds().size() != 1) return;
            // Get embed
            MessageEmbed embed = message.getEmbeds().get(0);
            // Check if sent by self
            if (!message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()))
            // Check if leaderboard embed
            if (!embed.getFooter().getText().contains("Page")) return;
            // Set new page to 0
            int newPage = 0;
            // Check if correct reaction
            if (event.getReaction().getEmote().getName().equals("\u27A1") ||
                event.getReaction().getEmote().getName().equals("\u2B05") ||
                event.getReaction().getEmote().getName().equals("\u274C")) {
                // Get if next or prev
                boolean next = event.getReaction().getEmote().getName().equals("\u27A1");
                // Check if delete
                if (event.getReaction().getEmote().getName().equals("\u274C")) {
                    // Delete message
                    message.delete().complete();
                    // Return
                    return;
                }
                // Get page
                int page = Integer.parseInt(embed.getFooter().getText().replace("Top Members - Page ", ""));
                // Check if next
                if (next) {
                    // Increment page
                    page++;
                } else {
                    // Decrement page
                    page--;
                }
                // Get embed
                MessageEmbed newEmbed = LeaderboardCommand.compileEmbed(message, new String[] {page + ""});
                // Edit message
                message.editMessage(newEmbed).complete();
                // Set new page
                newPage = page;
            }
            // Reset reactions
            LeaderboardCommand.resetReactions(message, newPage);
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
