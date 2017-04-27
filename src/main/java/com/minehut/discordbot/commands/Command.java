package com.minehut.discordbot.commands;

import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public abstract class Command {

    private String name;
    private String[] aliases;
    private String usage;
    private CommandType type;

    public enum CommandType {
        GENERAL,
        TRUSTED,
        MUSIC,
        MASTER
    }

    protected Command(String name, CommandType type, String usage, String... aliases) {
        this.name = name;
        this.aliases = aliases;
        this.usage = usage;
        this.type = type;
    }

    public abstract boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException;

    /**
     * Get the name of the command used after the prefix
     *
     * @return The command name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the aliases of the command
     *
     * @return The command aliases
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Gets the type of the command
     *
     * @return the command type
     */
    public CommandType getType() {
        return type;
    }

    /**
     * Gets the usage of the command
     *
     * @return the usage for the command
     */
    public String getUsage() {
        return getName() + " " + usage;
    }

}
