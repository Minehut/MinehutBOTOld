package com.minehut.discordbot.commands;

import com.minehut.discordbot.Core;

import java.util.List;

/**
 * Made by the developers of FlareBot.
 * Changed by MatrixTunnel on 12/27/2016.
 */
public enum CommandType {

    GENERAL,
    TRUSTED,
    MUSIC,
    MASTER;

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static CommandType[] getTypes() {
        return new CommandType[]{GENERAL, TRUSTED, MUSIC, MASTER};
    }

    public List<Command> getCommands() {
        return Core.getCommandsByType(this);
    }
}
