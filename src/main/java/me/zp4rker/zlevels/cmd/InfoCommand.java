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
        embed.setTitle("ZLevels");

        embed.addBlankField(false);

        embed.addField("Name", "ZLevels", false);
        embed.addField("Description", "An open-source Discord bot, which adds a levelling system to your server.", false);
        embed.addField("Version", ZLevels.VERSION, false);
        embed.addField("Commands", ZLevels.handler.getCommands().size() + "", false);
        embed.addField("Author", "<@145064570237485056>", false);

        embed.setColor(Color.decode(Config.EMBED_COLOUR));
    }

}
