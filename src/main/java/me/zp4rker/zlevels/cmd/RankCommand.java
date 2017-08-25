package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.ICommand;
import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.UserData;
import me.zp4rker.zlevels.util.LevelsUtil;
import static me.zp4rker.zlevels.util.MessageUtil.*;
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
    public void onCommand(Message message, String[] args) {
        if (args.length == 1) {
            if (message.getMentionedUsers().size() != 1) {
                try {
                    int rank = Integer.parseInt(args[0]);

                    UserData data = UserData.fromRank(rank);

                    sendEmbed(message.getJDA().getUserById(data.getUserId()), message, data);
                } catch (Exception e) {
                    selfDestruct(sendError("Invalid arguments!", "Usage: ```-rank @User``` Or ```" +
                            "-rank <RankNumber>```", message), Config.ERROR_LENGTH);
                }

                return;
            }

            if (message.getMentionedUsers().get(0).isBot()) {
                selfDestruct(sendError("Invalid user!", "Bots do not have ranks!", message), Config.ERROR_LENGTH);
                return;
            }

            User user = message.getMentionedUsers().get(0);

            UserData data = UserData.fromId(message.getMentionedUsers().get(0).getId());

            sendEmbed(user, message, data);
        } else if (args.length == 0) {

            UserData data = UserData.fromId(message.getAuthor().getId());

            sendEmbed(message.getAuthor(), message, data);
        } else {
            selfDestruct(sendError("Invalid arguments!", "Usage: ```-rank @User``` Or ```" +
                    "-rank <RankNumber>```", message), Config.ERROR_LENGTH);
        }
    }

    private void sendEmbed(User user, Message message, UserData data) {
        if (data == null) {
            selfDestruct(sendError("Invalid data!", "Could not get rank for **" + user.getName()
                    + "**!", message), Config.ERROR_LENGTH);
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.setColor(Color.decode(Config.EMBED_COLOUR));

        int[] rank = data.getRank();
        String levelXp = LevelsUtil.remainingXp(data.getTotalXp()) + "/" + LevelsUtil.xpToNextLevel(data.getLevel());

        embed.addField("User", data.getUsername(), false);

        embed.addField("Rank", rank[0] + "/" + rank[1], false);

        embed.addField("Level", data.getLevel() + "", false);

        embed.addField("XP", levelXp + " (Total: " + data.getTotalXp() + ")", false);

        message.getChannel().sendMessage(embed.build()).complete();
    }

}
