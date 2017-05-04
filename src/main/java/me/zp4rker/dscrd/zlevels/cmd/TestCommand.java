package me.zp4rker.dscrd.zlevels.cmd;

import me.zp4rker.dscrd.core.command.CommandExecutor;
import me.zp4rker.dscrd.core.command.RegisterCommand;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ZP4RKER
 */
public class TestCommand implements CommandExecutor {

    @RegisterCommand(aliases = "testtop")
    public String onCommand(Message message, String[] args) {
        // Return null
        return null;
    }

}
