package com.minehut.discordbot.commands.music;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by the SwagBot developers
 * Changed by MatrixTunnel on 1/9/2017.
 */
public class SkipCommand implements Command {

    @Override
    public String getCommand() {
        return "skip";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"next", "stopplayingthissongplease"};
    }

    public static List<String> votes = new ArrayList<>();
    private static int maxSkips = 0;

    @Override
    public void onCommand(IShard shard, IGuild guild, IChannel channel, IUser sender, IMessage message, String[] args) {
        Chat.setAutoDelete(message, 5);

        IVoiceChannel voiceChannel = message.getGuild().getConnectedVoiceChannel();
        Player player = Core.getMusicManager().getPlayer(channel.getGuild().getID());

        if (channel.getGuild().getConnectedVoiceChannel() == null || player.getPlayingTrack() == null) {
            Chat.sendMessage("The player is not playing!", channel, 15);
            return;
        }
        if (args.length == 1 && args[0].equals("force") && Bot.isTrusted(sender)) {
            votes.clear();
            Chat.sendMessage(sender.mention() + " Force skipped **" + player.getPlayingTrack().getTrack().getInfo().title + "**", channel, 15);
            player.skip();
            return;
        }
        if (!sender.getConnectedVoiceChannels().contains(channel.getGuild().getConnectedVoiceChannel())) {
            Chat.sendMessage(sender.mention() + " you must be in the channel in order to skip songs!", channel, 15);
            return;
        }
        if (votes.contains(sender.getID())) {
            Chat.sendMessage(sender.mention() + " you have already voted to skip this song!", channel, 15);
            return;
        }
        votes.add(sender.getID());

        if (voiceChannel != null && maxSkips != -1) {
            if (voiceChannel.getConnectedUsers().size() > 2) {
                maxSkips = (int) ((voiceChannel.getConnectedUsers().size() - 1) * 2 / 3.0 + 0.5);
                voiceChannel.getConnectedUsers().stream().filter(IUser::isDeafLocally).forEach(user -> maxSkips = maxSkips - 1);
            } else {
                maxSkips = 1;
            }

            if (maxSkips - votes.size() <= 0 || maxSkips == -1) {
                votes.clear();
                Chat.sendMessage("Skipped **" + player.getPlayingTrack().getTrack().getInfo().title + "**", channel, 25); //TODO Make command format better :P
                player.skip();
            } else {
                Chat.sendMessage(sender.mention() + " voted to skip!\n **" +
                        (maxSkips - votes.size()) + "** more votes are required to skip the current song.", channel, 25); //TODO Make command format better :P
            }
        }
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public CommandType getType() {
        return null;
    }
}
