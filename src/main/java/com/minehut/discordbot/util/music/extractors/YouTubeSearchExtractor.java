package com.minehut.discordbot.util.music.extractors;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.mashape.unirest.http.Unirest;
import com.minehut.discordbot.Core;
import com.minehut.discordbot.util.Chat;
import org.json.JSONArray;
import org.json.JSONObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.net.URLEncoder;

/**
 * Made by the FlareBot developers
 * Few small changes by MatrixTunnel
 */
public class YouTubeSearchExtractor extends YouTubeExtractor {
    public static final String SEARCH_URL = "https://www.youtube.com/results?search_query=";

    @Override
    public void process(String input, Player player, IMessage message, IUser user) throws Exception {
        JSONArray results = Unirest.get(String.format("https://www.googleapis.com/youtube/v3/search" +
                        "?q=%s&part=snippet&key=%s&type=video,playlist",
                URLEncoder.encode(input, "UTF-8"), Core.getYoutubeKey())).asJson().getBody()
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
            Chat.editMessage(user.mention(), Chat.getEmbed()
                    .withDesc(String.format("No results for `%s` could be found! Please try again with a different search term", input))
                    .withColor(Chat.CUSTOM_RED), message, 15);
            return;
        }
        super.process(link, player, message, user);
    }
}
