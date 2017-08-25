package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.ICommand;
import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.zlevels.ZLevels;
import me.zp4rker.zlevels.config.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;

/**
 * @author ZP4RKER
 */
public class InfoCommand implements ICommand {

    @RegisterCommand(aliases = "info",
            description = "Displays info about the bot.",
            usage = "{prefix}info")
    public void onCommand(Message message) {
        message.delete().queue();

        EmbedBuilder embed = new EmbedBuilder();
        compileEmbed(embed);

        message.getTextChannel().sendMessage(embed.build()).complete();
    }

    private void compileEmbed(EmbedBuilder embed) {
        embed.setAuthor("ZLevels", "https://github.com/ZP4RKER/zlevels", null);

        embed.addField("Name", "ZLevels", true);
        embed.addField("Version", ZLevels.VERSION, true);
        embed.addField("Commands", ZLevels.handler.getCommands().size() + "", true);
        embed.addField("Author", "<@145064570237485056>", true);
        embed.addField("Description", "An open-source Discord bot, which adds a levelling system to your server.", false);

        embed.setColor(Color.decode(Config.EMBED_COLOUR));
    }

}
