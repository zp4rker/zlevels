package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.ICommand;
import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.UserData;
import me.zp4rker.zlevels.util.LevelsUtil;
import me.zp4rker.zlevels.util.MessageUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

/**
 * @author ZP4RKER
 */
public class RankCommand implements ICommand {

    @RegisterCommand(aliases = "rank",
                    usage = "{prefix}rank <@User | Rank#>",
                    description = "Displays the specified user's rank and XP.")
    public String onCommand(Message message, String[] args) {
        // Check args
        if (args.length >= 1) {
            // Check mentions
            if (message.getMentionedUsers().size() != 1) {
                try {
                    // Get rank from args
                    int rank = Integer.parseInt(args[0]);
                    // Get userdata
                    UserData data = UserData.fromRank(rank);
                    // Send embed
                    sendEmbed(message.getJDA().getUserById(data.getUserId()), message, data);
                } catch (Exception e) {
                    // Send error
                    MessageUtil.sendError("Invalid arguments!", "Invalid arguments! \nUsage: ```-rank @User``` Or ```" +
                            "-rank <RankNumber>```", message);
                }
                // Return
                return null;
            }
            // Check if bot
            if (message.getMentionedUsers().get(0).isBot()) {
                // Send error
                MessageUtil.sendError("Invalid user!", "Bots do not have ranks!", message);
                // Return null
                return null;
            }
            // Get Mentioned user
            User user = message.getMentionedUsers().get(0);
            // Get userdata
            UserData data = UserData.fromId(message.getMentionedUsers().get(0).getId());
            // Send emebed
            sendEmbed(user, message, data);
        } else if (args.length == 0) {
            // Get userdata
            UserData data = UserData.fromId(message.getAuthor().getId());
            // Send embed
            sendEmbed(message.getAuthor(), message, data);
        } else {
            // Send error
            MessageUtil.sendError("Invalid arguments!", "Invalid arguments! \nUsage: ```-rank @User``` Or ```" +
                    "-rank <RankNumber>```", message);
        }
        // Return null
        return null;
    }

    private void sendEmbed(User user, Message message, UserData data) {
        // Check if null
        if (data == null) {
            // Send error
            MessageUtil.sendError("Invalid data!", "Could not get rank for **" + user.getName()
                    + "**!", message);
            // Return
            return;
        }
        // Get avatar url
        String url = data.getAvatarUrl();
        // Create embed
        EmbedBuilder embed = new EmbedBuilder();
        // Set author
        //embed.setAuthor(user.getName(), null, url);
        // Set footer
        embed.setFooter(Config.NAME + " Ranks", message.getJDA().getSelfUser().getEffectiveAvatarUrl());
        // Set colour
        embed.setColor(Color.decode(Config.EMBED_COLOUR));
        // Get rank
        int[] rank = data.getRank();
        // Add rank
        embed.addField("Rank", rank[0] + "/" + rank[1], false);
        // Add level
        embed.addField("Level", data.getLevel() + "", false);
        // Compile string
        String levelXp = LevelsUtil.remainingXp(data.getTotalXp()) + "/" + LevelsUtil.xpToNextLevel(data
                .getLevel());
        // Add xp
        embed.addField("XP", levelXp, false);
        // Add total xp
        embed.addField("Total XP", data.getTotalXp() + "", false);
        // Send embed
        ZLogger.debug("Start.");
        message.getChannel().sendMessage(embed.build()).complete();
        ZLogger.debug("End.");
    }

}
