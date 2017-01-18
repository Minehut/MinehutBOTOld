package com.minehut.discordbot.commands.music;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.arsenarsen.lavaplayerbridge.player.Track;
import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.music.extractors.YouTubeExtractor;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public void onCommand(IShard shard, IGuild guild, IChannel channel, IUser sender, IMessage message, String[] args) throws DiscordException {
        Chat.setAutoDelete(message, 5);

        Player player = Core.getMusicManager().getPlayer(channel.getGuild().getID());

        if (!player.getPlaylist().isEmpty()) {
            if (args.length == 1 && args[0].equals("clear") && Bot.isTrusted(sender)) {
                SkipCommand.votes.clear();
                Chat.sendMessage(sender.mention() + " Cleared the current playlist.", channel, 15);
                player.getPlaylist().clear();
                return;
            }

            List<String> songs = new ArrayList<>();
            int i = 1;
            StringBuilder sb = new StringBuilder();
            Iterator<Track> it = player.getPlaylist().iterator();
            while (it.hasNext() && songs.size() < 25) {
                Track next = it.next();
                String toAppend = String.format("**%s.** [`%s`](%s) added by <@!%s>\n", i++,
                        next.getTrack().getInfo().title, YouTubeExtractor.WATCH_URL + next.getTrack().getIdentifier(), next.getMeta().get("requester"));
                if (sb.length() + toAppend.length() > 1024) {
                    songs.add(sb.toString());
                    sb = new StringBuilder();
                }
                sb.append(toAppend);
            }
            songs.add(sb.toString());
            EmbedBuilder builder = Chat.getEmbed().withTitle("Playlist Queue");
            i = 1;
            for (String s : songs) {
                builder.appendField("\u2063", s, false);
            }

            Chat.sendMessage(builder.appendField("Total songs: ", String.valueOf(player.getPlaylist().size()), true)
                    .appendField("Volume: ", String.valueOf(player.getVolume()) + "%", true)
                    .appendField("Paused: ", String.valueOf(player.getPaused()).toLowerCase().replace("true", ":white_check_mark:").replace("false", ":x:"), true), channel, 25);
        } else {
            Chat.sendMessage(Chat.getEmbed().withDesc("There are no songs in the queue!").withColor(Chat.CUSTOM_RED), channel, 15);
        }
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}
