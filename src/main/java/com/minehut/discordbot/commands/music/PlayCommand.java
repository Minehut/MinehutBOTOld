package com.minehut.discordbot.commands.music;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.arsenarsen.lavaplayerbridge.player.Track;
import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.commands.management.ToggleMusicCommand;
import com.minehut.discordbot.exceptions.CommandException;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.GuildSettings;
import com.minehut.discordbot.util.music.VideoThread;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Made by the developers of FlareBot.
 * Changed by MatrixTunnel on 1/8/2017.
 */
public class PlayCommand extends Command {

    public PlayCommand() {
        super("play", new String[]{}, " <term>", CommandType.MUSIC);
    }

    @Override
    public boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        Player player = Core.getMusicManager().getPlayer(guild.getId());

        if (!ToggleMusicCommand.canQueue.get(guild.getId()) && !GuildSettings.isTrusted(sender)) {
            Chat.sendMessage(sender.getAsMention() + " Music commands are currently disabled. " +
                    "If you believe this is an error, please contact a staff member", channel, 10);
            return true;
        }

        if (args.length == 0) {
            return false;
        } else if (args.length >= 1) {
            if (guild.getSelfMember().getVoiceState().getChannel() == null) {
                Chat.sendMessage(sender.getAsMention() + " The bot is not in a voice channel!", channel, 10);
                return true;
            }
            if (!guild.getSelfMember().getVoiceState().getChannel().equals(sender.getVoiceState().getChannel())) {
                Chat.sendMessage(sender.getAsMention() + " You must be in the music channel in order to play songs!", channel, 10);
                return true;
            }

            if (args[0].startsWith("http") || args[0].startsWith("www.")) {
                if (!GuildSettings.isTrusted(sender)) {
                    if (!player.getPlaylist().isEmpty()) {
                        for (Track track : player.getPlaylist()) {
                            if (track.getTrack().getInfo().uri.equals(args[0])) {
                                Chat.sendMessage(sender.getAsMention() + " That song is already in the playlist!", channel, 10);
                                return true;
                            }
                        }

                        int tracks = 0;
                        for (Track track : player.getPlaylist()) {
                            if (sender.getUser().getId().equals(track.getMeta().get("requester").toString())) {
                                if (tracks >= 3) {
                                    Chat.sendMessage(sender.getAsMention() + " You have already queued the max of **4** songs in the playlist! Please try again later", channel, 10);
                                    return true;
                                }
                                tracks++;
                            }
                        }

                    }
                    if (player.getPlayingTrack() != null && player.getPlayingTrack().getTrack().getInfo().uri.equals(args[0])) {
                        Chat.sendMessage(sender.getAsMention() + " That song is already in the playing!", channel, 10);
                        return true;
                    }
                }
                VideoThread.getThread(args[0], channel, sender).start();
            } else {
                StringBuilder term = new StringBuilder();
                for (String s : args) {
                    term.append(s).append(" ");
                }
                term = new StringBuilder(term.toString().trim());
                VideoThread.getSearchThread(term.toString(), channel, sender).start(); //YouTube only
            }

        }

        return true;
    }

}
