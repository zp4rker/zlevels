package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.core.command.ICommand;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.UserData;
import me.zp4rker.zlevels.util.LevelsUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

/**
 * @author ZP4RKER
 */
public class LeaderboardCommand implements ICommand {

    @RegisterCommand(aliases = {"leaderboard", "lb"},
                    usage = "{prefix}leaderboard <Page #>",
                    description = "Displays the specified page of top members.")
    public String onCommand(Message message, String[] args) {
        // Send embed
        Message newMessage = message.getChannel().sendMessage(compileEmbed(message, args)).complete();
        // Catch errors
        try {
            // Add reactions
            resetReactions(newMessage, Integer.parseInt(args[0]));
        } catch (Exception e) {
            // Add reactions (page 1)
            resetReactions(newMessage, 1);
        }
        // Delete old message
        message.delete().complete();
        // Return null
        return null;
    }

    public static MessageEmbed compileEmbed(Message message, String[] args) {
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
            embed.setFooter("Top Members - Page " + index, null);
            // Compile description
            String desc = compileBoard(index, message);
            // Set description
            embed.setDescription(desc);
        } catch (Exception e) {
            // Set footer
            embed.setFooter("Top Members - Page 1", null);
            // Compile description
            String desc = compileBoard(1, message);
            // Set description
            embed.setDescription(desc);
        }
        // Return embed
        return embed.build();
    }

    private static String compileBoard(int index, Message message) {
        // Compile description
        StringBuilder desc = new StringBuilder();
        // Loop through until count
        for (int i = 0; i < 10; i++) {
            // Get data
            UserData data = LevelsUtil.getPage(index).get(i);
            User user = message.getGuild().getMemberById(data.getUserId()).getUser();
            // Add rank #
            desc.append("**" + data.getRank()[0] + "**. " + (i < 10 ? " " : ""))
                    // Add user name
                    .append("`" + user.getName() + "#")
                    .append(user.getDiscriminator() + "`")
                    // Add level
                    .append(" (Lvl. " + data.getLevel() + ")")
                    // Add new line
                    .append("\n\n");
        }
        return desc.toString();
    }

    public static void resetReactions(Message message, int page) {
        // Remove all reactions
        message.clearReactions().complete();
        // Check if above 1
        if (page > 1) {
            // Add previous button
            message.addReaction("\u2B05").complete();
        }
        // Add next button
        message.addReaction("\u27A1").complete();
        // Add delete button
        message.addReaction("\u274C").complete();
    }

}
