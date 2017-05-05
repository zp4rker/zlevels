package me.zp4rker.zlevels.lstnr;

import me.zp4rker.zlevels.ZLevels;
import me.zp4rker.zlevels.db.UserData;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

/**
 * @author ZP4RKER
 */
public class MemberLeaveListener {

    @SubscribeEvent
    public void onMemberLeave(GuildMemberLeaveEvent event) {
        // Get user id
        String id = event.getMember().getUser().getId();
        // Get user data
        UserData data = UserData.fromId(id);
        // Check if null
        if (data == null) return;
        // Run asynchronously
        ZLevels.async.submit(() -> {
            // Delete the data
            data.delete();
        });
    }

}
