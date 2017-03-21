package com.zp4rker.zlevels.commands;

import com.zp4rker.zlevels.core.cmd.CommandExecutor;
import com.zp4rker.zlevels.core.cmd.RegisterCommand;
import com.zp4rker.zlevels.core.db.UserData;
import com.zp4rker.zlevels.core.config.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.util.concurrent.Executors;

/**
 * @author ZP4RKER
 */
public class LeaderboardCommand implements CommandExecutor {

    @RegisterCommand(aliases = {"leaderboard", "top"})
    public String onCommand(Message message, String[] args) {
        // Run asynchronously
        Executors.newSingleThreadExecutor().submit(() -> {
            // Send embed
            sendEmbed(message, args);
        });
        // Return null
        return null;
    }

    private void sendEmbed(Message message, String[] args) {
        // Create embed
        EmbedBuilder embed = new EmbedBuilder();
        // Set author
        embed.setAuthor(Config.NAME + " All time leaderboard", null, null);
        // Set colour
        embed.setColor(Color.decode("#FFD700"));
        // Catch errors
        try {
            // Get count
            int count = Integer.parseInt(args[0]);
            // Check count
            if (count > 50) throw new Exception();
            // Set footer
            embed.setFooter("Top " + count, message.getJDA().getSelfUser().getAvatarUrl());
            // Compile description
            String desc = compileBoard(count, message);
            // Set description
            embed.setDescription(desc);
        } catch (Exception e) {
            // Set footer
            embed.setFooter("Top 10", message.getJDA().getSelfUser().getAvatarUrl());
            // Compile description
            String desc = compileBoard(10, message);
            // Set description
            embed.setDescription(desc);
        }
        // Send embed
        message.getChannel().sendMessage(embed.build()).complete();
    }

    private String compileBoard(int count, Message message) {
        // Compile description
        String desc = "";
        // Loop through until count
        for (int i = 0; i < count; i++) {
            // Get data
            UserData data = UserData.getAllData().get(i);
            // Add rank #
            desc += "**" + (i + 1) + "**. " + (i < 10 ? " " : "");
            // Add user name
            desc += message.getGuild().getMemberById(data.getUserId()).getAsMention();
            // Add level
            desc += " (Lvl. " + data.getLevel() + ")";
            // Check if last
            if ((i + 1) != count) {
                // Add new line
                desc += "\n\n";
            }
        }
        return desc;
    }

}
