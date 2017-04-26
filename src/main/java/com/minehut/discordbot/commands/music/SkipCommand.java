package com.minehut.discordbot.commands.music;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.commands.management.ToggleMusicCommand;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.GuildSettings;
import net.dv8tion.jda.core.entities.*;

import java.util.List;

/**
 * Made by the developer of SwagBot.
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

    public static List<String> votes;
    private static int maxSkips = 0;

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message);

        Player player = Core.getMusicManager().getPlayer(guild.getId());
        VoiceChannel voiceChannel = guild.getSelfMember().getVoiceState().getChannel();

        if (!ToggleMusicCommand.canQueue.get(guild.getId()) && !GuildSettings.isTrusted(sender)) {
            Chat.sendMessage(sender.getAsMention() + " Music commands are currently disabled. " +
                    "If you believe this is an error, please contact a staff member", channel, 10);
            return;
        }

        if (!guild.getAudioManager().isConnected() ||
                Core.getMusicManager().getPlayer(guild.getId()).getPlayingTrack() == null) {
            Chat.sendMessage("The player is not playing!", channel, 15);
            return;
        }
        if (args.length == 1 && args[0].equals("force") && GuildSettings.isTrusted(sender)) {
            votes.clear();
            Chat.sendMessage(sender.getAsMention() + " Force skipped **" + player.getPlayingTrack().getTrack().getInfo().title + "**", channel, 15);
            player.skip();
            return;
        }
        if (guild.getSelfMember().getVoiceState().getChannel() == null) {
            Chat.sendMessage(sender.getAsMention() + " The bot is not in a voice channel!", channel, 10);
            return;
        }
        if (!guild.getSelfMember().getVoiceState().getChannel().equals(sender.getVoiceState().getChannel())) {
            Chat.sendMessage(sender.getAsMention() + " you must be in the channel in order to skip songs!", channel, 10);
            return;
        }
        if (sender.getUser() == Core.getClient().getUserById(player.getPlayingTrack().getMeta().get("requester").toString())) {
            votes.clear();
            Chat.sendMessage("Skipped **" + player.getPlayingTrack().getTrack().getInfo().title + "**", channel, 20);
            player.skip();
            return;
        }
        if (votes.contains(sender.getUser().getId())) {
            Chat.sendMessage(sender.getAsMention() + " you have already voted to skip this song!", channel, 10);
            return;
        }
        votes.add(sender.getUser().getId());

        if (voiceChannel != null && maxSkips != -1) {
            if (voiceChannel.getMembers().size() > 2) {
                maxSkips = (int) ((voiceChannel.getMembers().size() - 1) * 2 / 3.0 + 0.5);
            } else {
                maxSkips = 1;
            }

            if (maxSkips - votes.size() <= 0 || maxSkips == -1) {
                votes.clear();
                Chat.sendMessage("Skipped **" + player.getPlayingTrack().getTrack().getInfo().title + "**", channel, 20);
                player.skip();
            } else {
                Chat.sendMessage(sender.getAsMention() + " voted to skip!\n **" +
                        (maxSkips - votes.size()) + "** more votes are required to skip the current song.", channel, 20);
            }
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}
