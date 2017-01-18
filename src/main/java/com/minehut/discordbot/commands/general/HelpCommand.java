package com.minehut.discordbot.commands.general;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by MatrixTunnel on 12/18/2016.
 */
public class HelpCommand implements Command {

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"commands"};
    }

    @Override
    public void onCommand(IShard shard, IGuild guild, IChannel channel, IUser sender, IMessage message, String[] args) {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
