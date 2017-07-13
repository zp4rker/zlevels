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
        String id = event.getMember().getUser().getId();
        UserData data = UserData.fromId(id);

        if (data == null) return;

        ZLevels.async.submit(() -> data.delete());
    }

}
