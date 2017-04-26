package com.minehut.discordbot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.minehut.discordbot.Core;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MatrixTunnel on 4/20/2017.
 */
public class GuildSettings {

    private static String file;
    public static String FILE_NAME = "guilds.json";

    public static String getFile() {
        return file;
    }

    public static TextChannel getLogChannel(Guild guild) {
        JSONArray array = new JSONArray(file);
        for (Object obj : array) {
            JSONObject object = (JSONObject) obj;
            if (object.getString("id").equals(guild.getId()) && object.getJSONObject("settings").optString("log-channel", null) != null) {
                return Core.getClient().getGuildById(guild.getId()).getTextChannelById(object.getJSONObject("settings").getString("log-channel"));
            }
        }
        return null;
    }

    public static List<String> getMusicCommandChannels() {
        List<String> channels = new ArrayList<>();
        JSONArray array = new JSONArray(file);
        for (Object obj : array) {
            JSONObject object = (JSONObject) obj;
            if (object.getJSONObject("settings").optString("music-channel", null) != null) {
                channels.add(object.getJSONObject("settings").getString("music-channel"));
            }
        }
        return channels;
    }

    public static Role getMutedRole(Guild guild) {
        JSONArray array = new JSONArray(file);
        for (Object obj : array) {
            JSONObject object = (JSONObject) obj;
            if (object.getString("id").equals(guild.getId()) && object.getJSONObject("settings").optString("muted-role", null) != null) {
                return Core.getClient().getGuildById(guild.getId()).getRoleById(object.getJSONObject("settings").getString("muted-role"));
            }
        }
        return null;
    }

    public static String getPrefix(Guild guild) {
        JSONArray array = new JSONArray(file);
        for (Object obj : array) {
            JSONObject object = (JSONObject) obj;
            if (object.getString("id").equals(guild.getId())) {
                return object.getJSONObject("settings").optString("prefix", "!");
            }
        }
        return "!";
    }

    public static boolean filterSpam(Guild guild) {
        JSONArray array = new JSONArray(file);
        for (Object obj : array) {
            JSONObject object = (JSONObject) obj;
            if (object.getString("id").equals(guild.getId())) {
                return object.getJSONObject("settings").optBoolean("anti-spam", false);
            }
        }
        return false;
    }

    public static boolean isTrusted(Member member) {
        JSONArray array = new JSONArray(file);
        for (Object obj : array) {
            JSONObject object = (JSONObject) obj;
            if (object.getString("id").equals(member.getGuild().getId()) && object.getJSONObject("settings").getJSONArray("trusted-roles") != null) {
                for (Object roleId : object.getJSONObject("settings").getJSONArray("trusted-roles")) {
                    if (Core.getDiscord().userHasRoleId(member, roleId.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void save(Object obj) throws IOException {
        BufferedWriter fout = new BufferedWriter(new FileWriter(FILE_NAME));
        fout.write(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
        fout.close();
    }

    public void load() throws IOException {
        Gson gson = new Gson();
        JsonElement json = gson.fromJson(new FileReader(FILE_NAME), JsonElement.class);

        file = gson.toJson(json);
        //Core.log.info(file.toString());
    }

}
