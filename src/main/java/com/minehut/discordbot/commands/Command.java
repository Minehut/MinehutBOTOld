package com.minehut.discordbot.commands;

import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

/**
 * Made by the FlareBot developers
 * Changed by MatrixTunnel on 12/16/2016.
 */
public interface Command {

    String getCommand();

    void onCommand(IShard shard, IGuild guild, IChannel channel, IUser sender, IMessage message, String[] args) throws DiscordException;

    String getDescription();

    CommandType getType();

    default String getPermission() {
        return null;
    }

    default String[] getAliases() {
        return new String[]{};
    }

    static String getPrefix() {
        return "!";
    }
}
