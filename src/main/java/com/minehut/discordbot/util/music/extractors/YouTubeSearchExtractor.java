package com.minehut.discordbot.util.music.extractors;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.arsenarsen.lavaplayerbridge.player.Track;
import com.mashape.unirest.http.Unirest;
import com.minehut.discordbot.Core;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.GuildSettings;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Made by the FlareBot developers
 * Few small changes by MatrixTunnel
 */
public class YouTubeSearchExtractor extends YouTubeExtractor {

    @Override
    public void process(String input, Player player, Message message, Member member) throws Exception {
        JSONArray results = Unirest.get(String.format("https://www.googleapis.com/youtube/v3/search" +
                        "?q=%s&part=snippet&key=%s&type=video,playlist",
                URLEncoder.encode(input, "UTF-8"), Core.getConfig().getGoogleAPIKey())).asJson().getBody()
                .getObject().getJSONArray("items");
        String link = null;
        for (Object res : results) {
            if (res instanceof JSONObject) {
                JSONObject result = (JSONObject) res;
                if (!result.getJSONObject("snippet").getString("liveBroadcastContent").contains("none"))
                    continue;
                JSONObject id = result.getJSONObject("id");
                if (id.getString("kind").equals("youtube#playlist")) {
                    link = PLAYLIST_URL + id.getString("playlistId");
                } else {
                    link = WATCH_URL + id.getString("videoId");
                }
                break;
            }
        }
        if (link == null) {
            Chat.editMessage(Chat.getEmbed()
                    .setDescription(String.format("No results for `%s` could be found! Please try again with a different search term", input))
                    .setColor(Chat.CUSTOM_RED).build(), message, 15);
            return;
        }
        if (!GuildSettings.isTrusted(member)) {
            if (player.getPlayingTrack() != null && player.getPlayingTrack().getTrack().getInfo().uri.equals(link)) {
                Chat.sendMessage(member.getAsMention() + " That song is already in the playing!", message.getChannel(), 10);
                return;
            }
            if (!player.getPlaylist().isEmpty()) {
                for (Track track : player.getPlaylist()) {
                    if (track.getTrack().getInfo().uri.equals(link)) {
                        Chat.sendMessage(member.getAsMention() + " That song is already in the playlist!", message.getChannel(), 10);
                        return;
                    }
                }

                int tracks = 0;
                for (Track track : player.getPlaylist()) {
                    if (member.getUser().getId().equals(track.getMeta().get("requester").toString())) {
                        if (tracks == 3) {
                            Chat.sendMessage(member.getAsMention() + " You have already queued the max of **4** songs in the playlist! Please try again later", message.getChannel(), 10);
                            return;
                        }
                        tracks++;
                    }
                }
            }
        }
        super.process(link, player, message, member);
    }
}
