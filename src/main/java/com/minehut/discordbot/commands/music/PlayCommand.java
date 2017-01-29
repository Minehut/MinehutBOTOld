package com.minehut.discordbot.commands.music;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.music.VideoThread;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Made by the FlareBot developers
 * Changed by MatrixTunnel on 1/8/2017.
 */
public class PlayCommand implements Command {

    @Override
    public String getCommand() {
        return "play";
    }

    @Override
    public void onCommand(IShard shard, IGuild guild, IChannel channel, IUser sender, IMessage message, String[] args) {
        Chat.setAutoDelete(message, 5);

        if (args.length == 0) {
            Chat.sendMessage(sender.mention() + " Usage: `" + Command.getPrefix() + getCommand() + getArgs() + "`", channel, 15);
        } else if (args.length >= 1) {
            if (!sender.getConnectedVoiceChannels().contains(guild.getConnectedVoiceChannel())) {
                Chat.sendMessage(sender.mention() + " you must be in the music channel in order to play songs!", channel, 10);
                return;
            }

            if (args[0].startsWith("http") || args[0].startsWith("www.")) {
                VideoThread.getThread(args[0], channel, sender).start();
            } else {
                String term = "";
                for (String s : args) {
                    term += s + " ";
                }
                term = term.trim();
                VideoThread.getSearchThread(term, channel, sender).start();
            }
        }

    }

    @Override
    public String getArgs() {
        return " <term>";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}
