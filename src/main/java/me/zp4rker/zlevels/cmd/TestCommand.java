package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.ICommand;
import me.zp4rker.core.command.RegisterCommand;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ZP4RKER
 */
public class TestCommand implements ICommand {

    @RegisterCommand(aliases = "testtop")
    public String onCommand(Message message, String[] args) {
        // Return null
        return null;
    }

}
