package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.core.command.ICommand;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.UserData;
import me.zp4rker.zlevels.util.LevelsUtil;
import me.zp4rker.zlevels.util.MessageUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.HashMap;

/**
 * @author ZP4RKER
 */
public class LeaderboardCommand implements ICommand {

    public static HashMap<String, String> issues = new HashMap<>();

    @RegisterCommand(aliases = {"leaderboard", "lb"},
                    usage = "{prefix}leaderboard <Page #>",
                    description = "Displays the specified page of top members.")
    public void onCommand(Message message, String[] args) {
        Message newMessage = message.getChannel().sendMessage(compileEmbed(message, args)).complete();
        issues.put(newMessage.getId(), message.getAuthor().getId());

        try {
            resetReactions(newMessage, Integer.parseInt(args[0]));
        } catch (Exception e) {
            resetReactions(newMessage, 1);
        }

        MessageUtil.bypassDeleteLogs(message, message.getChannel().sendMessage("`").complete());
    }

    public static MessageEmbed compileEmbed(Message message, String[] args) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("All time leaderboard for " + Config.NAME, null, null);
        embed.setColor(Color.decode(Config.EMBED_COLOUR));

        try {
            int index = Integer.parseInt(args[0]);

            if (index > LevelsUtil.getPageCount() || index < 1) throw new Exception();

            embed.setFooter("Top Members - Page " + index, message.getJDA().getSelfUser().getEffectiveAvatarUrl());

            String desc = compileBoard(index, message);
            embed.setDescription(desc);
        } catch (Exception e) {
            embed.setFooter("Top Members - Page 1", null);

            String desc = compileBoard(1, message);
            embed.setDescription(desc);
        }

        return embed.build();
    }

    private static String compileBoard(int index, Message message) {
        StringBuilder desc = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            UserData data = LevelsUtil.getPage(index).get(i);
            User user = message.getGuild().getMemberById(data.getUserId()).getUser();

            desc.append("**").append(data.getRank()[0]).append("**. ").append((i < 10 ? " " : ""))
                    .append("`").append(user.getName()).append("#").append(user.getDiscriminator()).append("`")
                    .append(" (Lvl. ").append(data.getLevel()).append(")")
                    .append("\n\n");
        }
        return desc.toString();
    }

    public static void resetReactions(Message message, int page) {
        message.clearReactions().complete();

        if (page > 1) {
            message.addReaction("\u2B05").complete();
        }

        message.addReaction("\u27A1").complete();

        message.addReaction("\u274C").complete();
    }

}
