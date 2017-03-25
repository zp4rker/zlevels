package com.zp4rker.zlevels.commands;

import com.zp4rker.zlevels.core.cmd.CommandExecutor;
import com.zp4rker.zlevels.core.cmd.RegisterCommand;
import com.zp4rker.zlevels.core.config.Config;
import com.zp4rker.zlevels.core.util.AutoRole;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;

/**
 * @author ZP4RKER
 */
public class RewardsCommand implements CommandExecutor {

    @RegisterCommand(aliases = "rewards")
    public String onCommand(JDA jda, Message message) {
        // Create embed
        EmbedBuilder embed = new EmbedBuilder();
        // Set author
        embed.setAuthor("Rewards", null, jda.getSelfUser().getEffectiveAvatarUrl());
        // Set colour
        embed.setColor(Color.decode(Config.EMBED_COLOUR));
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
        // Return null
        return null;
    }

}
