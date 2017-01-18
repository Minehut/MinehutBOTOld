package com.minehut.discordbot.util.music.extractors;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.arsenarsen.lavaplayerbridge.player.Track;
import com.minehut.discordbot.util.Chat;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;

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
    public void process(String input, Player player, IMessage message, IUser user) throws Exception {
        AudioItem item;
        try {
            item = player.resolve(input);
            if (item == null) {
                Chat.editMessage("", Chat.getEmbed()
                        .withDesc("Could not get that video/playlist! Make sure the URL is correct!")
                        .withColor(Chat.CUSTOM_RED), message, 15);
                return;
            }
        } catch (RuntimeException e) {
            Chat.editMessage("", Chat.getEmbed()
                    .withDesc("Could not get that video/playlist!")
                    .withColor(Chat.CUSTOM_RED).appendField("YouTube said: ", e.getMessage(), true), message, 15);
            return;
        }
        List<AudioTrack> tracks = new ArrayList<>();
        String name;
        if (item instanceof AudioPlaylist) {
            AudioPlaylist audioPlaylist = (AudioPlaylist) item;
            tracks.addAll(audioPlaylist.getTracks());
            name = audioPlaylist.getName();
        } else {
            AudioTrack track = (AudioTrack) item;
            if (track.getInfo().length == 0 || track.getInfo().isStream) {
                EmbedBuilder builder = Chat.getEmbed().withColor(Chat.CUSTOM_RED).withDesc("Cannot queue a livestream!");
                Chat.editMessage("", builder, message, 15);
                return;
            }
            tracks.add(track);
            name = track.getInfo().title;
        }
        if (name != null) {
            for (AudioTrack t : tracks) {
                Track track = new Track(t);
                track.getMeta().put("requester", user.getID());
                player.queue(track);
            }
            EmbedBuilder builder = Chat.getEmbed();
            builder.withDesc(String.format("%s queued the %s [`%s`](%s)", user.mention(), tracks.size() == 1 ? "song" : "playlist",
                    name, input));
            if (tracks.size() > 1)
                builder.appendField("Song count:", String.valueOf(tracks.size()), true);
            Chat.editMessage("", builder, message, 20);
        }
    }

    @Override
    public boolean valid(String input) {
        return input.matches(ANY_YT_URL);
    }
}
