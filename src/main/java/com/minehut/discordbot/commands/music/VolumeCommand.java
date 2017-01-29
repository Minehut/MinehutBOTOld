package com.minehut.discordbot.commands.music;

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
 * Created by MatrixTunnel on 1/27/2017.
 */
public class VolumeCommand implements Command {

    @Override
    public String getCommand() {
        return "volume";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"vol", "sound"};
    }

    @Override
    public void onCommand(IShard shard, IGuild guild, IChannel channel, IUser sender, IMessage message, String[] args) throws DiscordException {
        Chat.setAutoDelete(message, 5);

        Chat.sendMessage(sender.mention() + "\nSince that command was overloading the CPU, is has been removed.\n" +
                "If you want to change the volume, do this instead: https://gfycat.com/UnrulyBountifulAfricancivet", channel, 15); //TODO Put link in message
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}
