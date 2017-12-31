package com.minehut.discordbot.util.music.extractors;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.arsenarsen.lavaplayerbridge.player.Track;
import com.minehut.discordbot.MinehutBot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Made by the FlareBot developers
 * Few small changes by MatrixTunnel
 */
public class YouTubeSearchExtractor extends YouTubeExtractor {

    @Override
    public void process(String input, Player player, Message message, UserClient client) throws Exception {
        ResponseBody body = MinehutBot.get().getHttpClient().newCall(new Request.Builder()
                .url(String.format("https://www.googleapis.com/youtube/v3/search?q=%s&part=snippet&key=%s&type=video,playlist",
                        URLEncoder.encode(input, "UTF-8"), MinehutBot.get().getConfig().getGoogleAPIKey()))
                .header("accept", "application/json").build())
                .execute().body();

        if (body == null) {
            message.editMessage(new MessageBuilder(EmbedBuilder.ZERO_WIDTH_SPACE).setEmbed(Chat.getEmbed()
                    .setDescription("There was an error while trying to queue your request. Please try again later")
                    .setColor(Chat.CUSTOM_RED).build()).build()).queue(msg -> Chat.removeMessage(msg, 15));
            return;
        }

        JSONArray results = new JSONObject(body.string()).getJSONArray("items");
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
            message.editMessage(new MessageBuilder(EmbedBuilder.ZERO_WIDTH_SPACE).setEmbed(Chat.getEmbed()
                    .setDescription(String.format("No results for `%s` could be found! Please try again with a different search term", input))
                    .setColor(Chat.CUSTOM_RED).build()).build()).queue(msg -> Chat.removeMessage(msg, 15));
            return;
        }
        if (!client.isStaff()) {
            if (player.getPlayingTrack() != null && player.getPlayingTrack().getTrack().getInfo().uri.equals(link)) {
                Chat.editMessage(client.getUser().getAsMention() + " That song is already in the playing!", message, 10);
                return;
            }
            if (!player.getPlaylist().isEmpty()) {
                for (Track track : player.getPlaylist()) {
                    if (track.getTrack().getInfo().uri.equals(link)) {
                        Chat.editMessage(client.getUser().getAsMention() + " That song is already in the playlist!", message, 10);
                        return;
                    }
                }

                int tracks = 0;
                for (Track track : player.getPlaylist()) {
                    if (client.getId().equals(track.getMeta().get("requester").toString())) {
                        if (tracks == MinehutBot.get().getConfig().getMaxPlaylistQueue() - 1) {
                            Chat.editMessage(client.getUser().getAsMention() + " You have already queued the max of **" + MinehutBot.get().getConfig().getMaxPlaylistQueue() + "** songs in the playlist! " +
                                    "Please try again later", message, 10);
                            return;
                        }
                        tracks++;
                    }
                }
            }
        }
        super.process(link, player, message, client);
    }
}
