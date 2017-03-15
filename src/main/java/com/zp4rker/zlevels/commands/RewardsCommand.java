package com.zp4rker.zlevels.commands;

import com.zp4rker.zlevels.cmd.CommandExecutor;
import com.zp4rker.zlevels.cmd.RegisterCommand;
import com.zp4rker.zlevels.util.AutoRole;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;

import java.util.concurrent.Executors;

/**
 * @author ZP4RKER
 */
public class RewardsCommand implements CommandExecutor {

    @RegisterCommand(aliases = "rewards")
    public String onCommand(JDA jda, Message message) {
        // Run async
        Executors.newSingleThreadExecutor().submit(() -> {
            // Create embed
            EmbedBuilder embed = new EmbedBuilder();
            // Set author
            embed.setAuthor("Rewards", null, jda.getSelfUser().getEffectiveAvatarUrl());
            // Start content
            String content = "";
            // Loop through roles
            for (int level : AutoRole.roles.keySet()) {
                // Get role name
                String role = AutoRole.roles.get(level).get("name").toString();
                // Start content
                content += "**VIP**\n";
                // Add info
                content += "When you reach level " + level + ", you will be rewarded with the " + role + " role.\n";
            }
            // Set content
            embed.setDescription(content);
            // Send embed
            message.getChannel().sendMessage(embed.build()).complete();
        });
        // Return null
        return null;
    }

}