package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.core.command.ICommand;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.UserData;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

/**
 * @author ZP4RKER
 */
public class TopCommand implements ICommand {

    @RegisterCommand(aliases = "top",
                    usage = "{prefix}top <Amount to list>",
                    description = "Displays the top users to the specified amount.")
    public void onCommand(Message message, String[] args) {
        sendEmbed(message, args);
    }

    private void sendEmbed(Message message, String[] args) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor(Config.NAME + " all time leaderboard", null, null);
        embed.setColor(Color.decode(Config.EMBED_COLOUR));

        try {
            int count = Integer.parseInt(args[0]);

            if (count > 50) throw new Exception();

            embed.setFooter("Top " + count, null);

            String desc = compileBoard(count, message);
            embed.setDescription(desc);
        } catch (Exception e) {
            int count = 10;

            if (UserData.getAllData().size() < 10) {
                count = UserData.getAllData().size();
            }

            embed.setFooter("Top " + count, message.getGuild().getIconUrl());

            String desc = compileBoard(count, message);
            embed.setDescription(desc);
        }

        message.getChannel().sendMessage(embed.build()).complete();
    }

    private String compileBoard(int count, Message message) {
        StringBuilder desc = new StringBuilder();

        for (int i = 0; i < count; i++) {
            UserData data = UserData.getAllData().get(i);

            desc.append("**").append(i + 1).append("**. ").append(i < 10 ? " " : "");

            User user = message.getGuild().getMemberById(data.getUserId()).getUser();
            desc.append("`").append(user.getName()).append("#").append(user.getDiscriminator()).append("`");
            desc.append(" (Lvl. ").append(data.getLevel()).append(")");

            if ((i + 1) != count) {
                desc.append("\n\n");
            }
        }

        return desc.toString();
    }

}
