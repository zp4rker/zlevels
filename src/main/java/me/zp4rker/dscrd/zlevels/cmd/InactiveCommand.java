package me.zp4rker.dscrd.zlevels.cmd;

import me.zp4rker.dscrd.core.command.CommandExecutor;
import me.zp4rker.dscrd.core.command.RegisterCommand;
import me.zp4rker.dscrd.zlevels.db.UserData;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ZP4RKER
 */
public class InactiveCommand implements CommandExecutor {

    private int count;

    @RegisterCommand(aliases = "inactive",
                    usage = "{prefix}inactive",
                    description = "Displays count of inactive members on the server.")
    public String onCommand(Message message) {
        // Create count
        count = 0;
        // Loop through
        message.getGuild().getMembers().forEach(member -> {
            // Check if bot
            if (member.getUser().isBot()) return;
            // Catch errors
            try {
                // Get data
                UserData data = UserData.fromId(member.getUser().getId());
                // Check if exists
                if (data == null) count++;
            } catch (Exception e) {
                // No data
            }
        });
        // Send message
        message.getTextChannel().sendMessage("There are " + count + " inactive " + (count == 1 ? "user" : "users") +
                " in this server.").queue();
        // Return null
        return null;
    }

}
