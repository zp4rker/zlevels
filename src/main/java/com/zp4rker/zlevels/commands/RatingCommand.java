package com.zp4rker.zlevels.commands;

import com.zp4rker.zlevels.core.cmd.CommandExecutor;
import com.zp4rker.zlevels.core.cmd.RegisterCommand;
import com.zp4rker.zlevels.core.db.StaffRating;
import com.zp4rker.zlevels.core.config.Config;
import com.zp4rker.zlevels.core.util.MessageUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.concurrent.Executors;

/**
 * @author ZP4RKER
 */
public class RatingCommand implements CommandExecutor {

    @RegisterCommand(aliases = "rating")
    public String onCommand(Message message, String[] args) {
        // Run asynchronously
        Executors.newSingleThreadExecutor().submit(() -> {
            // Check if staff
            if (message.getGuild().getMember(message.getAuthor()).getRoles().stream().noneMatch(role -> role.getName()
                    .equals(Config.STAFF_ROLE))) {
                // Send error
                MessageUtil.sendError("Invalid permissions!", "You are not authorised to run that command!", message);
                // Return
                return;
            }
            // Check arguments
            if (args.length == 0) {
                // Get user
                User user = message.getAuthor();
                // Send embed
                sendEmbed(user, message);
            } else if (args.length >= 1) {
                // Check for mentions
                if (message.getMentionedUsers().size() != 1) {
                    // Send error
                    MessageUtil.sendError("Invalid arguments!", "Invalid Arguments!\nUsage: ```-rating @User```",
                            message);
                    // Return
                    return;
                }
                // Get user
                User user = message.getMentionedUsers().get(0);
                // Send embed
                sendEmbed(user, message);
            } else {
                // Send error
                MessageUtil.sendError("Invalid arguments!", "Invalid Arguments!\nUsage: ```-rating @User```",
                        message);
            }
        });
        return null;
    }

    private void sendEmbed(User user, Message message) {
        // Get the staff rating
        StaffRating rating = StaffRating.fromId(user.getId());
        // Check if exists
        if (rating == null) {
            // Send error
            MessageUtil.sendError("Invalid data!", "Could not get a staff rating for **" + user.getName() + "**!",
                    message);
            // Return
            return;
        }
        // Create embed
        EmbedBuilder embed = new EmbedBuilder();
        // Set author
        embed.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
        // Set colour
        embed.setColor(Color.decode(Config.EMBED_COLOUR));
        // Compile ratings string
        String ratings = rating.getRatings() + (rating.getRatings() == 1 ? " rating." : " ratings.");
        // Add ratings field
        embed.addField("All Ratings", ratings, false);
        // Compile monthly ratings string
        String monthly = rating.getMonthlyRatings() + (rating.getMonthlyRatings() == 1 ? " rating." : " ratings.");
        // Add monthly ratings field
        embed.addField("Monthly Ratings", monthly, false);
        // Set footer
        embed.setFooter("Staff Rating", message.getJDA().getSelfUser().getEffectiveAvatarUrl());
        // Send embed
        message.getTextChannel().sendMessage(embed.build()).complete();
    }

}
