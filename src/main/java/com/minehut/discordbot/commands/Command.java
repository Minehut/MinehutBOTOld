package com.minehut.discordbot.commands;

import com.minehut.discordbot.Core;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

/**
 * Made by the FlareBot developers
 * Changed by MatrixTunnel on 12/16/2016.
 */
public interface Command {

    String getCommand();

    void onCommand(JDA jda, Guild guild, TextChannel channel, Member member, User sender, Message message, String[] args);

    String getArgs();

    CommandType getType();

    default String[] getAliases() {
        return new String[]{};
    }

    static String getPrefix() {
        return Core.getConfig().getCommandPrefix();
    }
}
