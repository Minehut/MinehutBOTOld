package com.minehut.discordbot.commands.manage;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by MatrixTunnel on 1/14/2017.
 */
public class ReconnectVoiceCommand implements Command {

    @Override
    public String getCommand() {
        return "reconnect";
    }

    @Override
    public void onCommand(IShard shard, IGuild guild, IChannel channel, IUser sender, IMessage message, String[] args) throws DiscordException {
        Chat.removeMessage(message);

        List<IVoiceChannel> channels = Core.getDiscord().getConnectedVoiceChannels();
        channels.forEach(IVoiceChannel::leave);
        Chat.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (String id : Bot.getMusicVoiceChannels()) {
                    Core.getDiscord().getVoiceChannelByID(id).join();
                }
            }
        }, 2000);
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
