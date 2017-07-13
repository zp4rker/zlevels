package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.core.command.ICommand;
import me.zp4rker.core.command.handler.CommandHandler;
import me.zp4rker.core.logger.ZLogger;
import me.zp4rker.zlevels.ZLevels;
import me.zp4rker.zlevels.config.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;

/**
 * @author ZP4RKER
 */
public class HelpCommand implements ICommand {

    @RegisterCommand(aliases = "help")
    public void onCommand(Message message) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("ZLevels Help - Command List", null, null);

        embed.setColor(Color.decode(Config.EMBED_COLOUR));

        embed.setFooter("Written by ZP4RKER", "https://github.com/ZP4RKER");

        embed.setDescription(compileList());

        try {
            message.getAuthor().openPrivateChannel().complete().sendMessage(embed.build()).complete();
        } catch (Exception e) {
            ZLogger.warn("Could not open DM channel or already open.");
        }
    }

    private String compileList() {
        StringBuilder sb = new StringBuilder();

        for (CommandHandler.Command command : ZLevels.handler.getCommands().values()) {
            if (command.getCommandAnnotation().aliases()[0].equals("help")) continue;

            sb.append(compileCommand(command));
        }

        return sb.toString();
    }

    private String compileCommand(CommandHandler.Command command) {
        String label = command.getCommandAnnotation().aliases()[0];

        String usage = command.getCommandAnnotation().usage().replace("{prefix}", Config.PREFIX);

        String desc = command.getCommandAnnotation().description();

        return "**" + label + "** - " + desc + "\n__Usage:__ `" + usage + "`\n\n";
    }

}
