package com.zp4rker.zlevels.commands;

import com.zp4rker.zlevels.core.cmd.CommandExecutor;
import com.zp4rker.zlevels.core.cmd.RegisterCommand;
import com.zp4rker.zlevels.core.db.UserData;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ZP4RKER
 */
public class InactiveCommand implements CommandExecutor {

    private int count;

    @RegisterCommand(aliases = "inactive")
    public String onCommand(Message message) {
        // Create count
        count = 0;
        // Loop through
        message.getGuild().getMembers().forEach(member -> {
            // Get data
            UserData data = UserData.fromId(member.getUser().getId());
            // Check if exists
            if (data == null) count++;
        });
        // Send message
        message.getTextChannel().sendMessage("There are " + count + " inactive " + (count == 1 ? "user" : "users") +
                " in this server.").queue();
        // Return null
        return null;
    }

}
