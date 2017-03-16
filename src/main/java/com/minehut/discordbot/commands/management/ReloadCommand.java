package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

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
    public void onCommand(JDA jda, Guild guild, TextChannel channel, Member member, User sender, Message message, String[] args) {
        Chat.removeMessage(message);

        try {
            Core.getConfig().load();
            Core.log.info("Config reloaded!");
        } catch (IOException e) {
            Core.log.info("Error reloading config", e);
        }
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMINISTRATIVE;
    }
}
