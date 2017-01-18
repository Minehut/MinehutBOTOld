package com.minehut.discordbot.commands;

import com.minehut.discordbot.Core;

import java.util.List;

/**
 * Made by the FlareBot developers
 * Changed by MatrixTunnel on 12/27/2016.
 */
public enum CommandType {

    GENERAL,
    ADMINISTRATIVE(false),
    MUSIC(false);

    private boolean dms;

    CommandType() {
        this(true);
    }

    CommandType(boolean dms) {
        this.dms = dms;
    }

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static CommandType[] getTypes() {
        return new CommandType[]{GENERAL, ADMINISTRATIVE, MUSIC};
    }

    public List<Command> getCommands() {
        return Core.getCommandsByType(this);
    }

    public boolean usableInDMs() {
        return dms;
    }

    public String formattedName() {
        return Character.toUpperCase(name().charAt(0)) + name().substring(1).toLowerCase();
    }
}
