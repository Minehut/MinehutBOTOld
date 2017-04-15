package com.minehut.discordbot.util.music.extractors;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.arsenarsen.lavaplayerbridge.player.Playlist;
import com.arsenarsen.lavaplayerbridge.player.Track;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Made by the FlareBot developers
 * Few small changes by MatrixTunnel
 */
public class YouTubeExtractor implements Extractor {
    public static final String YOUTUBE_URL = "https://www.youtube.com";
    public static final String PLAYLIST_URL = "https://www.youtube.com/playlist?list=";
    public static final String WATCH_URL = "https://www.youtube.com/watch?v=";
    public static final String ANY_YT_URL = "(?:https?://)?(?:(?:(?:(?:(?:www\\.)|(?:m\\.))?(?:youtube\\.com))/(?:(?:watch\\?v=([^?&\\n]+)(?:&(?:[^?&\\n]+=(?:[^?&\\n]+)))*)|(?:playlist\\?list=([^&?]+))(?:&[^&]*=[^&]+)?))|(?:youtu\\.be/(.*)))";

    @Override
    public Class<? extends AudioSourceManager> getSourceManagerClass() {
        return YoutubeAudioSourceManager.class;
    }

    @Override
    public void process(String input, Player player, Message message, User user) throws Exception { //TODO Redo these messages to be more user friendly
        AudioItem item;
        try {
            item = player.resolve(input);
            if (item == null) {
                Chat.editMessage(Chat.getEmbed()
                        .setDescription("Could not get that video/playlist! Make sure the URL is correct!")
                        .setColor(Chat.CUSTOM_RED).build(), message, 15);
                return;
            }
        } catch (RuntimeException e) {
            Chat.editMessage(Chat.getEmbed()
                    .setDescription("Could not get that video/playlist!")
                    .setColor(Chat.CUSTOM_RED).addField("YouTube said: ", e.getMessage(), true).build(), message, 15);
            return;
        }
        List<AudioTrack> audioTracks = new ArrayList<>();
        String name;
        if (item instanceof AudioPlaylist) {
            if (Bot.isTrusted(user)) {
                AudioPlaylist audioPlaylist = (AudioPlaylist) item;
                audioTracks.addAll(audioPlaylist.getTracks());
                name = audioPlaylist.getName();
            } else {
                EmbedBuilder builder = Chat.getEmbed().setColor(Chat.CUSTOM_RED).setDescription("That playlist could not be queued! If you want this queued, please ask a staff member");
                Chat.editMessage(builder.build(), message, 15);
                return;
            }
        } else {
            AudioTrack track = (AudioTrack) item;
            if (track.getInfo().length >= 900000 && !Bot.isTrusted(user)) {
                EmbedBuilder builder = Chat.getEmbed().setColor(Chat.CUSTOM_RED).setDescription("That track could not be queued! The video length is too long");
                Chat.editMessage(builder.build(), message, 15);
                return;
            }
            if (track.getInfo().length == 0 || track.getInfo().isStream) {
                EmbedBuilder builder = Chat.getEmbed().setColor(Chat.CUSTOM_RED).setDescription("Livestreams cannot be queued!");
                Chat.editMessage(builder.build(), message, 15);
                return;
            }
            audioTracks.add(track);
            name = track.getInfo().title;
        }
        if (name != null) {
            List<Track> tracks = audioTracks.stream().map(Track::new).map(track -> {
                track.getMeta().put("requester", user.getId());
                track.getMeta().put("guildId", player.getGuildId());
                return track;
            }).collect(Collectors.toList());
            if (tracks.size() > 1) { // Double `if` https://giphy.com/gifs/ng1xAzwIkDgfm
                Playlist p = new Playlist(tracks);
                player.queue(p);
            } else {
                player.queue(tracks.get(0));
            }
            EmbedBuilder builder = Chat.getEmbed();
            builder.setDescription(String.format("%s queued the %s [`%s`](%s)", user.getAsMention(), tracks.size() == 1 ? "song" : "playlist", name, input));
            if (audioTracks.size() > 1)
                builder.addField("Song count:", String.valueOf(tracks.size()), true);
            Chat.editMessage(builder.build(), message, 20);
        }
    }

    @Override
    public boolean valid(String input) {
        return input.matches(ANY_YT_URL);
    }
}
