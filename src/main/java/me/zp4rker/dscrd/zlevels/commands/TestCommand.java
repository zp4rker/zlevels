package me.zp4rker.dscrd.zlevels.commands;

import me.zp4rker.dscrd.core.cmd.CommandExecutor;
import me.zp4rker.dscrd.core.cmd.RegisterCommand;
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