package me.zp4rker.zlevels.lstnr;

import me.zp4rker.zlevels.ZLevels;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.UserData;
import me.zp4rker.zlevels.util.LevelsUtil;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author ZP4RKER
 */
public class MessageSendListener {

    private static final List<String> spamFilter = new ArrayList<>();

    @SubscribeEvent
    public void onMessage(MessageReceivedEvent event) {
        if (event.getGuild() == null) return;
        if (!event.getGuild().getId().equals(Config.SERVER)) return;
        if (event.getAuthor().isBot()) return;

        if (!spamFilter.contains(event.getAuthor().getId())) {
            if (event.getMessage().getContent().startsWith("-")) return;

            ZLevels.async.submit(() -> {
                UserData data = UserData.fromId(event.getAuthor().getId());

                if (data == null) {
                    data = new UserData();
                    data.setUserId(event.getAuthor().getId());
                }

                long randomXp = LevelsUtil.randomXp(10, 25);

                data.setTotalXp(data.getTotalXp() + randomXp);
                data.save();

                spamFilter.add(event.getAuthor().getId());

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        spamFilter.remove(event.getAuthor().getId());
                    }
                }, 1000 * 60);
            });
        }
    }

}
