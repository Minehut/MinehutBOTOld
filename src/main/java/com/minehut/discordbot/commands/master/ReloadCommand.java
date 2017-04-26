package com.minehut.discordbot.commands.master;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.GuildSettings;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.IOException;

/**
 * Created by MatrixTunnel on 2/27/2017.
 */
public class ReloadCommand implements Command {

    @Override
    public String getCommand() {
        return "reload";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rl"};
    }

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message);

        try {
            Core.getConfig().load();
            Core.log.info("Config reloaded!");
        } catch (IOException e) {
            Core.log.info("Error reloading config", e);
        }

        try {
            new GuildSettings().load();
            Core.log.info("Guild Settings reloaded!");
        } catch (IOException e) {
            Core.log.info("Error reloading guild settings", e);
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.MASTER;
    }
}
