package com.minehut.discordbot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MatrixTunnel on 4/26/2017.
 */
public class Config {

    private int maxMessageLength;
    private String mainGuildID, mainMusicChannelID, punishmentLogID, discordToken, googleAPIKey, secretKey;
    private List<String> blockedUsers;

    public static String FILE_NAME = "settings.json";

    public Config()  {
        maxMessageLength = 275;
        mainGuildID = "";
        mainMusicChannelID = "";
        punishmentLogID = "";

        discordToken = "";
        googleAPIKey = "";
        secretKey = "";

        blockedUsers = new ArrayList<>();
    }

    public static void save(Object obj) throws IOException {
        BufferedWriter fout;
        fout = new BufferedWriter(new FileWriter(FILE_NAME));
        fout.write(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
        fout.close();
    }

    public void load() throws IOException {
        RandomAccessFile fin;
        byte[] buffer;

        fin = new RandomAccessFile(FILE_NAME, "r");
        buffer = new byte[(int) fin.length()];
        fin.readFully(buffer);
        fin.close();

        String json = new String(buffer);
        Config file = new Gson().fromJson(json, Config.class);
        maxMessageLength = file.maxMessageLength;
        mainGuildID = file.mainGuildID;
        mainMusicChannelID = file.mainMusicChannelID;
        punishmentLogID = file.punishmentLogID;

        discordToken = file.discordToken;
        googleAPIKey = file.googleAPIKey;
        secretKey = file.secretKey;

        blockedUsers = file.blockedUsers;
    }

    public int getMaxMessageLength() {
        return maxMessageLength;
    }

    public String getMainGuildID() {
        return mainGuildID;
    }

    public String getMainMusicChannelID() {
        return mainMusicChannelID;
    }

    public String getPunishmentLogID() {
        return punishmentLogID;
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public String getGoogleAPIKey() {
        return googleAPIKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public List<String> getBlockedUsers() {
        return blockedUsers;
    }
}
