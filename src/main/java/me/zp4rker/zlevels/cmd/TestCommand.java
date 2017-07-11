package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.ICommand;
import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.UserData;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

/**
 * @author ZP4RKER
 */
public class TestCommand implements ICommand {

    @RegisterCommand(aliases = "testtop")
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
            // Get count
            int count = Integer.parseInt(args[0]);
            // Check count
            if (count > 50) throw new Exception();
            // Set footer
            embed.setFooter("Top " + count, null);
            // Compile description
            String desc = compileBoard(count, message);
            // Set description
            embed.setDescription(desc);
        } catch (Exception e) {
            // Set count to 10
            int count = 10;
            // Check if less than 10
            if (UserData.getAllData().size() < 10) {
                // Set to total
                count = UserData.getAllData().size();
            }
            // Set footer
            embed.setFooter("Top " + count, message.getGuild().getIconUrl());
            // Compile description
            String desc = compileBoard(count, message);
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
            User user = message.getGuild().getMemberById(data.getUserId()).getUser();
            // Add user name
            desc += "`" + user.getName() + "#" + user.getDiscriminator() + "`";
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
