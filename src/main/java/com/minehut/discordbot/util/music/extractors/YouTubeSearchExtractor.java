package com.minehut.discordbot.util.music.extractors;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.mashape.unirest.http.Unirest;
import com.minehut.discordbot.Core;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Made by the FlareBot developers
 * Few small changes by MatrixTunnel
 */
public class YouTubeSearchExtractor extends YouTubeExtractor {

    @Override
    public void process(String input, Player player, Message message, User user) throws Exception {
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
        super.process(link, player, message, user);
    }
}
