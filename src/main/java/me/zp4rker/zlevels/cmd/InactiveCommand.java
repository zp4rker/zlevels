package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.core.command.ICommand;
import me.zp4rker.zlevels.db.UserData;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ZP4RKER
 */
public class InactiveCommand implements ICommand {

    private int count;

    @RegisterCommand(aliases = "inactive",
                    usage = "{prefix}inactive",
                    description = "Displays count of inactive members on the server.")
    public void onCommand(Message message) {
        count = 0;

        message.getGuild().getMembers().forEach(member -> {
            if (member.getUser().isBot()) return;

            try {
                UserData data = UserData.fromId(member.getUser().getId());

                if (data == null) count++;
            } catch (Exception e) {
                // No data
            }
        });

        message.getTextChannel().sendMessage("There are " + count + " inactive " + (count == 1 ? "user" : "users") +
                " in this server.").queue();
    }

}
