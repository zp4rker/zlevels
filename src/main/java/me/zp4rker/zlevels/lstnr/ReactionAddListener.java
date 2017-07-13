package me.zp4rker.zlevels.lstnr;

import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.zlevels.ZLevels;
import me.zp4rker.zlevels.cmd.LeaderboardCommand;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.StaffRating;
import me.zp4rker.zlevels.db.UserData;
import me.zp4rker.zlevels.util.LevelsUtil;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Handles events when reactions are added.
 *
 * @author ZP4RKER
 */
public class ReactionAddListener {

    private static final List<String> spamFilter = new ArrayList<>();

    /**
     * Handles the reaction add event.
     *
     * @param event The event to handle.
     */
    @SubscribeEvent
    public void onReaction(MessageReactionAddEvent event) {
        ZLevels.async.submit(() -> {
            try {
                if (!event.getChannel().getType().equals(ChannelType.TEXT)) return;

                Message message = event.getChannel().getMessageById(event.getMessageId()).complete();

                if (!message.getGuild().getId().equals(Config.SERVER)) return;
                if (event.getChannel().getMessageById(event.getMessageId()).complete().getAuthor().isBot()) return;
                if (event.getChannel().getMessageById(event.getMessageId()).complete().getAuthor().equals(event.getUser())) return;
                if (spamFilter.contains(event.getUser().getId())) return;
                if (alreadyReacted(message, event)) return;

                UserData data = UserData.fromId(message.getAuthor().getId());

                if (data == null) {
                    data = new UserData();
                    data.setUserId(message.getAuthor().getId());
                }

                long randomXp = LevelsUtil.randomXp(15, 25);
                data.setTotalXp(data.getTotalXp() + randomXp);

                data.save();

                spamFilter.add(event.getUser().getId());
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        spamFilter.remove(event.getUser().getId());
                    }
                }, 1000 * 60);
            } catch (Exception e) {
                ZLogger.warn("Could not handle ReactionAddEvent correctly!");
            }
        });

        ZLevels.async.submit(() -> {
            if (!Config.RATINGS_ENABLED) return;

            try {
                if (!event.getChannel().getType().equals(ChannelType.TEXT)) return;
                if (!Config.CHANNELS_FOR_RATINGS.contains(event.getChannel().getId())) return;
                if (event.getChannel().getMessageById(event.getMessageId()).complete().getAuthor().isBot()) return;
                if (event.getChannel().getMessageById(event.getMessageId()).complete().getAuthor().equals(event.getUser())) return;

                Message message = event.getChannel().getMessageById(event.getMessageId()).complete();

                if (alreadyReacted(message, event)) return;

                Guild guild = ((TextChannel) event.getChannel()).getGuild();
                User user = message.getAuthor();

                if (guild.getMember(user).getRoles().stream()
                        .noneMatch(role -> role.getName().equals(Config.STAFF_ROLE))) return;

                StaffRating rating = StaffRating.fromId(user.getId());

                if (rating == null) {
                    rating = new StaffRating();
                    rating.setUserId(user.getId());
                }

                rating.setRatings(rating.getRatings() + 1);
                rating.setMonthlyRatings(rating.getMonthlyRatings() + 1);

                rating.save();
            } catch (Exception e) {
                ZLogger.warn("Could not handle ReactionAddEvent correctly!");
            }
        });

        ZLevels.async.submit(() -> {
            Message message = event.getChannel().getMessageById(event.getMessageId()).complete();

            if (event.getUser().getId().equals(event.getJDA().getSelfUser().getId())) return;
            if (message.getEmbeds().size() != 1) return;

            MessageEmbed embed = message.getEmbeds().get(0);

            if (!message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) return;
            if (!embed.getFooter().getText().contains("Page")) return;

            int page = Integer.parseInt(embed.getFooter().getText().replace("Top Members - Page ", ""));
            if (!LeaderboardCommand.issues.get(message.getId()).equals(event.getUser().getId())) {
                LeaderboardCommand.resetReactions(message, page);
                return;
            }

            int newPage = 0;

            if (event.getReaction().getEmote().getName().equals("\u27A1") ||
                event.getReaction().getEmote().getName().equals("\u2B05") ||
                event.getReaction().getEmote().getName().equals("\u274C")) {
                boolean next = event.getReaction().getEmote().getName().equals("\u27A1");

                if (event.getReaction().getEmote().getName().equals("\u274C")) {
                    message.delete().complete();
                    return;
                }

                if (next) {
                    page++;
                } else {
                    page--;
                }

                MessageEmbed newEmbed = LeaderboardCommand.compileEmbed(message, new String[] {page + ""});
                message.editMessage(newEmbed).complete();
                newPage = page;
            }

            LeaderboardCommand.resetReactions(message, newPage);
        });
    }

    private boolean alreadyReacted(Message message, MessageReactionAddEvent event) {
        return message.getReactions().stream().anyMatch(reaction -> {
            try {
                return !reaction.equals(event.getReaction()) &&
                        reaction.getUsers().complete().contains(event.getUser());
            } catch (Exception e) {
                return false;
            }
        });
    }

}
