package me.zp4rker.zlevels.cmd;

import me.zp4rker.core.command.ICommand;
import me.zp4rker.core.command.RegisterCommand;
import me.zp4rker.zlevels.config.Config;
import me.zp4rker.zlevels.util.AutoRole;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;

/**
 * The rewards command.
 *
 * @author ZP4RKER
 */
public class RewardsCommand implements ICommand {

    /**
     * The method to handle the command.
     *
     * @param jda The JDA instance.
     * @param message The message sent.
     */
    @RegisterCommand(aliases = "rewards",
                    usage = "{prefix}rewards",
                    description = "Displays the possible rewards from levelling up.")
    public void onCommand(JDA jda, Message message) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("Rewards", null, jda.getSelfUser().getEffectiveAvatarUrl());
        embed.setColor(Color.decode(Config.EMBED_COLOUR));

        String content = "";
        for (int level : AutoRole.roles.keySet()) {
            String role = AutoRole.roles.get(level).get("name").toString();

            content += "**" + role + "**\n";
            content += "When you reach level " + level + ", you will be rewarded with the " + role + " role.\n";
        }
        embed.setDescription(content);

        message.getChannel().sendMessage(embed.build()).complete();
    }

}
