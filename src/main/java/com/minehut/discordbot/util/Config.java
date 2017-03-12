package com.minehut.discordbot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

/**
 * Originally created by Zachary Kozar (masterzach32) for SwagBot.
 * Changed by MatrixTunnel on 2/4/2017.
 */
public class Config {

    private int shardCount, maxMessageLength;
    private String mainGuildID, logChannelID, mutedRoleID, commandPrefix, discordToken, googleAPIKey, secretKey;
    private String[] trustedRoles, blockedUsers, musicVoiceChannels, musicCommandChannels;
    private String BOT_JSON = "settings.json";

    /**
     * Creates a reference to the preferences file, and generates one if it doesn't exist.
     * @throws IOException
     */
    public Config() throws IOException {
        shardCount = 1;
        maxMessageLength = 250;
        mainGuildID = "";
        logChannelID = "";
        mutedRoleID = "";
        commandPrefix = "!";

        discordToken = "";
        googleAPIKey = "";
        secretKey = "";

        trustedRoles = new String[]{"", ""};
        blockedUsers = new String[]{"", ""};
        musicVoiceChannels = new String[]{"", ""};
        musicCommandChannels = new String[]{"", ""};

        File prefs = new File(BOT_JSON);
        if (!prefs.exists()) {
            prefs.createNewFile();
            save(this);
        }
    }

    public void save(Object obj) throws IOException {
        BufferedWriter fout;
        fout = new BufferedWriter(new FileWriter(BOT_JSON));
        fout.write(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
        fout.close();
    }

    public void load() throws IOException {
        RandomAccessFile fin;
        byte[] buffer;

        fin = new RandomAccessFile(BOT_JSON, "r");
        buffer = new byte[(int) fin.length()];
        fin.readFully(buffer);
        fin.close();

        String json = new String(buffer);
        Config file = new Gson().fromJson(json, Config.class);
        shardCount = file.shardCount;
        maxMessageLength = file.maxMessageLength;
        mainGuildID = file.mainGuildID;
        logChannelID = file.logChannelID;
        mutedRoleID = file.mutedRoleID;
        commandPrefix = file.commandPrefix;
        discordToken = file.discordToken;
        googleAPIKey = file.googleAPIKey;
        secretKey = file.secretKey;

        trustedRoles = file.trustedRoles;
        blockedUsers = file.blockedUsers;
        musicVoiceChannels = file.musicVoiceChannels;
        musicCommandChannels = file.musicCommandChannels;
    }

    public int getShardCount() {
        return shardCount;
    }

    public int getMaxMessageLength() {
        return maxMessageLength;
    }

    public String getMainGuildID() {
        return mainGuildID;
    }

    public String getLogChannelID() {
        return logChannelID;
    }

    public String getMutedRoleID() {
        return mutedRoleID;
    }

    public String getCommandPrefix() {
        return commandPrefix;
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

    public String[] getTrustedRoles() {
        return trustedRoles;
    }

    public String[] getBlockedUsers() {
        return blockedUsers;
    }

    public String[] getMusicVoiceChannels() {
        return musicVoiceChannels;
    }

    public String[] getMusicCommandChannels() {
        return musicCommandChannels;
    }
}
