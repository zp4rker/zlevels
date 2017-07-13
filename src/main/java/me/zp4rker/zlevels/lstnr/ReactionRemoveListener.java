package me.zp4rker.zlevels.lstnr;

import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.zlevels.ZLevels;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.StaffRating;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

/**
 * Handles events when reactions are removed.
 *
 * @author ZP4RKER
 */
public class ReactionRemoveListener {

    /**
     * Handles the reaction remove event.
     *
     * @param event The event to handle.
     */
    @SubscribeEvent
    public void onReactionRemove(MessageReactionRemoveEvent event) {
        ZLevels.async.submit(() -> {
            if (!Config.RATINGS_ENABLED) return;

            try {
                Message message = event.getChannel().getMessageById(event.getMessageId()).complete();

                if (!message.getGuild().getId().equals(Config.SERVER)) return;
                if (message.getGuild().getMember(message.getAuthor()).getRoles().stream().noneMatch(role -> role
                        .getName().equals(Config.STAFF_ROLE))) return;
                if (!Config.CHANNELS_FOR_RATINGS.contains(event.getChannel().getId())) return;

                StaffRating rating = StaffRating.fromId(message.getAuthor().getId());

                if (rating == null) return;

                if (rating.getRatings() > 0) {
                    rating.setRatings(rating.getRatings() - 1);

                    if (rating.getMonthlyRatings() > 0) {
                        rating.setMonthlyRatings(rating.getMonthlyRatings() - 1);
                    }
                }

                rating.save();
            } catch (Exception e) {
                ZLogger.warn("Could not handle ReactionRemoveEvent correctly!");
            }
        });
    }

}
