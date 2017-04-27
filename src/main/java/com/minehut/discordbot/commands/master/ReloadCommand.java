package com.minehut.discordbot.commands.master;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.GuildSettings;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.IOException;


public class ReloadCommand extends Command {

    public ReloadCommand() {
        super("reload", CommandType.MASTER, null, "rl");
    }

    @Override
    public boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        try {
            Core.getConfig().load();
            Core.log.info("Config reloaded!");
        } catch (IOException e) {
            Core.log.error("Error reloading config", e);
            throw new CommandException("Error reloading config");
        }

        try {
            new GuildSettings().load();
            Core.log.info("Guild Settings reloaded!");
        } catch (IOException e) {
            Core.log.error("Error reloading guild settings", e);
            throw new CommandException("Error reloading config");
        }

        return true;
    }

}
