package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.core.command.ICommand;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.StaffRating;
import me.zp4rker.zlevels.util.MessageUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

/**
 * @author ZP4RKER
 */
public class RatingCommand implements ICommand {

    @RegisterCommand(aliases = {"rating", "ratings"},
                    usage = "{prefix}rating @User",
                    description = "Displays the ratings for the specified staff member.")
    public void onCommand(Message message, String[] args) {
        if (args.length == 0) {
            if (message.getGuild().getMember(message.getAuthor()).getRoles().stream().noneMatch(role -> role.getName()
                    .equals(Config.STAFF_ROLE))) {
                MessageUtil.sendError("You are not staff!", "Only staff members have ratings.", message);
                return;
            }

            User user = message.getAuthor();

            sendEmbed(user, message);
        } else if (args.length >= 1) {
            if (message.getMentionedUsers().size() != 1) {
                MessageUtil.sendError("Invalid arguments!", "Invalid Arguments!\nUsage: ```-rating @User```",
                        message);
                return;
            }

            User user = message.getMentionedUsers().get(0);

            if (message.getGuild().getMember(user).getRoles().stream().noneMatch(role -> role.getName()
                    .equals(Config.STAFF_ROLE))) {
                MessageUtil.sendError("That member is not staff!", "Only staff members have ratings.", message);
                return;
            }

            sendEmbed(user, message);
        } else {
            MessageUtil.sendError("Invalid arguments!", "Invalid Arguments!\nUsage: ```-rating @User```",
                    message);
        }
    }

    private void sendEmbed(User user, Message message) {
        StaffRating rating = StaffRating.fromId(user.getId());

        if (rating == null) {
            MessageUtil.sendError("Invalid data!", "Could not get a staff rating for **" + user.getName() + "**!",
                    message);
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
        embed.setColor(Color.decode(Config.EMBED_COLOUR));

        String ratings = rating.getRatings() + (rating.getRatings() == 1 ? " rating." : " ratings.");
        embed.addField("All Ratings", ratings, false);

        String monthly = rating.getMonthlyRatings() + (rating.getMonthlyRatings() == 1 ? " rating." : " ratings.");
        embed.addField("Monthly Ratings", monthly, false);

        embed.setFooter("Staff Rating", message.getJDA().getSelfUser().getEffectiveAvatarUrl());

        message.getTextChannel().sendMessage(embed.build()).complete();
    }

}
