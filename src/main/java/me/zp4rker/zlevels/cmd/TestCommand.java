package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.ICommand;
import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.db.UserData;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

/**
 * @author ZP4RKER
 */
public class TestCommand implements ICommand {

    @RegisterCommand(aliases = "test")
    public void onCommand(Message message, String[] args) {
        UserData.flushCache();
        message.delete().complete();
    }

}
