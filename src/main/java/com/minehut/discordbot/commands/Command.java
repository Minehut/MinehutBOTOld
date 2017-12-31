package com.minehut.discordbot.commands;

import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public abstract class Command {

    private CommandType type;
    private String usage;
    private String[] commands;

    public enum CommandType {
        GENERAL,
        TRUSTED,
        MUSIC,
        MASTER
    }

    protected Command(CommandType type, String usage, String... commands) {
        this.type = type;
        this.usage = usage;
        this.commands = commands;
    }

    public abstract boolean onCommand(UserClient sender, Guild guild, TextChannel channel, Message message, String[] args) throws CommandException;

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
        return commands[0] + " " + usage;
    }

    /**
     * Gets the commands
     *
     * @return the commands
     */
    public String[] getCommands() {
        return commands;
    }

}
