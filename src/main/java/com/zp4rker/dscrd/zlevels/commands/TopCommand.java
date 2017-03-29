package com.zp4rker.dscrd.zlevels.commands;

import com.zp4rker.dscrd.core.cmd.CommandExecutor;
import com.zp4rker.dscrd.core.cmd.RegisterCommand;
import com.zp4rker.dscrd.zlevels.core.config.Config;
import com.zp4rker.dscrd.zlevels.core.db.UserData;
import com.zp4rker.dscrd.zlevels.core.util.LevelsUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;

/**
 * @author ZP4RKER
 */
public class TopCommand implements CommandExecutor {

    @RegisterCommand(aliases = {"top", "leaderboard"},
                    usage = "{prefix}top <Amount to list>",
                    description = "Displays the top users to the specified amount.")
    public String onCommand(Message message, String[] args) {
        // Send embed
        sendEmbed(message, args);
        // Return null
        return null;
    }

    private void sendEmbed(Message message, String[] args) {
        // Create embed
        EmbedBuilder embed = new EmbedBuilder();
        // Set author
        embed.setAuthor(Config.NAME + " All time leaderboard", null, null);
        // Set colour
        embed.setColor(Color.decode(Config.EMBED_COLOUR));
        // Catch errors
        try {
            // Get index
            int index = Integer.parseInt(args[0]);
            // Check count
            if (index > LevelsUtil.getPageCount() || index < 1) throw new Exception();
            // Set footer
            embed.setFooter("Top Members - Page " + index, message.getJDA().getSelfUser().getAvatarUrl());
            // Compile description
            String desc = compileBoard(index, message);
            // Set description
            embed.setDescription(desc);
        } catch (Exception e) {
            // Set footer
            embed.setFooter("Top Members - Page 1", message.getJDA().getSelfUser().getAvatarUrl());
            // Compile description
            String desc = compileBoard(1, message);
            // Set description
            embed.setDescription(desc);
        }
        // Send embed
        message.getChannel().sendMessage(embed.build()).complete();
    }

    private String compileBoard(int index, Message message) {
        // Compile description
        StringBuilder desc = new StringBuilder();
        // Loop through until count
        for (int i = 0; i < 10; i++) {
            // Get data
            UserData data = LevelsUtil.getPage(index - 1).get(i);
            // Add rank #
            desc.append("**" + data.getRank()[0] + "**. " + (i < 10 ? " " : ""))
                    // Add user name
                    .append(message.getGuild().getMemberById(data.getUserId()).getAsMention())
                    // Add level
                    .append(" (Lvl. " + data.getLevel() + ")");
            // Check if last
            if ((i + 1) != index) {
                // Add new line
                desc.append("\n\n");
            }
        }
        return desc.toString();
    }

}
