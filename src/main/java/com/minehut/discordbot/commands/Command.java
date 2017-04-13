package com.minehut.discordbot.commands;

import com.minehut.discordbot.Core;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Made by the developers of FlareBot.
 * Changed by MatrixTunnel on 12/16/2016.
 */
public interface Command {

    String getCommand();

    void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args);

    CommandType getType();

    default String[] getAliases() {
        return new String[]{};
    }

    static String getPrefix() {
        return Core.getConfig().getCommandPrefix();
    }
}
