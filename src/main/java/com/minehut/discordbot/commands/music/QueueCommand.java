package com.minehut.discordbot.commands.music;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.arsenarsen.lavaplayerbridge.player.Track;
import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

/**
 * Made by the FlareBot developers
 * Changed by MatrixTunnel on 1/8/2017.
 */
public class QueueCommand implements Command {

    @Override
    public String getCommand() {
        return "queue";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"songs", "playlist", "songlist"};
    }

    @Override
    public void onCommand(JDA jda, Guild guild, TextChannel channel, Member member, User sender, Message message, String[] args) {
        Chat.setAutoDelete(message, 5);

        Player player = Core.getMusicManager().getPlayer(channel.getGuild().getId());

        if (!player.getPlaylist().isEmpty()) {
            if (Bot.isTrusted(sender)) {
                if (args.length == 1 && args[0].equals("clear")) {
                    Chat.sendMessage(sender.getAsMention() + " Cleared the current playlist.", channel, 15);
                    player.getPlaylist().clear();
                    return;
                } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
                    int number;
                    try {
                        number = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        Chat.sendMessage("That is an invalid number!", channel, 5);
                        return;
                    }

                    Queue<Track> queue = Core.getMusicManager().getPlayer(channel.getGuild().getId()).getPlaylist();

                    if (number < 1 || number > queue.size()) {
                        Chat.sendMessage(sender.getAsMention() + " There is no song with that index. Songs in queue: **" + queue.size() + "**", channel, 5);
                        return;
                    }

                    List<Track> playlist = new ArrayList<>(queue);
                    playlist.remove(number - 1);
                    queue.clear();
                    queue.addAll(playlist);

                    Chat.sendMessage(sender.getAsMention() + " Removed song **#" + number + "** from the queue!", channel, 15);
                    return;
                }
            }

            List<String> songs = new ArrayList<>();
            int i = 1;
            StringBuilder sb = new StringBuilder();
            Iterator<Track> it = player.getPlaylist().iterator();
            while (it.hasNext() && songs.size() < 25) {
                Track next = it.next();

                String toAppend; //TODO Add something to show soundcloud and youtube songs separate from each other
                if (next.getTrack() instanceof YoutubeAudioTrack) {
                    toAppend = String.format("**%s.** [%s](%s) `[%s]` | <@!%s>\n", i++, next.getTrack().getInfo().title,
                            next.getTrack().getInfo().uri, Bot.millisToTime(next.getTrack().getDuration()), next.getMeta().get("requester"));
                } else if (next.getTrack() instanceof SoundCloudAudioTrack) {
                    toAppend = String.format("**%s.** [%s](%s) `[%s]` | <@!%s>\n", i++, next.getTrack().getInfo().title,
                            next.getTrack().getInfo().uri, Bot.millisToTime(next.getTrack().getDuration()), next.getMeta().get("requester"));
                } else {
                    toAppend = String.format("**%s.** [%s](%s) `[%s]` | <@!%s>\n", i++, next.getTrack().getInfo().title,
                            next.getTrack().getInfo().uri, Bot.millisToTime(next.getTrack().getDuration()), next.getMeta().get("requester"));
                }

                if (sb.length() + toAppend.length() > 1024) {
                    songs.add(sb.toString());
                    sb = new StringBuilder();
                }
                sb.append(toAppend);
            }
            songs.add(sb.toString());
            EmbedBuilder builder = Chat.getEmbed().setTitle("Playlist Queue", null);
            i = 1;
            for (String s : songs) {
                builder.addField("\u200e", s, false);
            }

            Chat.sendMessage(builder.addField("Total songs: ", String.valueOf(player.getPlaylist().size()), true)
                    .addField("Volume: ", player.getVolume() + "%", true)
                    .addField("Paused: ", player.getPaused() ? ":white_check_mark:" : ":x:", true), channel, 25);
        } else {
            Chat.sendMessage(Chat.getEmbed().setDescription("There are no songs in the queue!").setColor(Chat.CUSTOM_RED), channel, 15);
        }
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
