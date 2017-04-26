package com.minehut.discordbot.commands;

import com.minehut.discordbot.exceptions.CommandException;
import com.minehut.discordbot.util.GuildSettings;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public abstract class Command {

    private String name;
    private String[] aliases;
    private CommandType type;
    protected Guild guild;

    protected Command(String name, String[] aliases, CommandType type) {
        this.name = name;
        this.aliases = aliases;
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
        return GuildSettings.getPrefix(guild) + getName();
    }

}
