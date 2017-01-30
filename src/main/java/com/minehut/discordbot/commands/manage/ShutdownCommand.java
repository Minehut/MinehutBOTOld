package com.minehut.discordbot.commands.manage;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

/**
 * Created by MatrixTunnel on 1/30/2017.
 */
public class ShutdownCommand implements Command {

    @Override
    public String getCommand() {
        return "shutdown";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"stop", "exit", "end"};
    }

    @Override
    public void onCommand(IShard shard, IGuild guild, IChannel channel, IUser sender, IMessage message, String[] args) throws DiscordException {
        Chat.removeMessage(message);

        if (args.length == 1 && args[0].equals("-r")) {
            Core.shutdown(true);
        } else {
            Core.shutdown(false);
        }
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMINISTRATIVE;
    }
}
