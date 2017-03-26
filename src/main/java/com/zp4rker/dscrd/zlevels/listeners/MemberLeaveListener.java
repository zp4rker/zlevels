package com.zp4rker.dscrd.zlevels.listeners;

import com.zp4rker.dscrd.zlevels.core.db.UserData;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.concurrent.Executors;

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
        Executors.newSingleThreadExecutor().submit(() -> {
            // Delete the data
            data.delete();
        });
    }

}
