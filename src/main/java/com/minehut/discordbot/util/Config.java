package com.minehut.discordbot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.*;

/**
 * Created by MatrixTunnel on 4/26/2017.
 */
@Getter @NoArgsConstructor
public class Config {

    private String mainGuildId, musicCommandChannelId, audioChannelId, commandChannelId, logChannelId, discordToken, googleAPIKey, secretKey, commandPrefix = "!", muteRoleName = "Muted";
    private int messageAlertLength = 275, maxPlaylistQueue = 4;
    private boolean muteEnabled = false;

    public static String FILE_NAME = "settings.json";

    public void save(Object obj) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
        writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
        writer.close();
    }

    public void load() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
        Config config = new Gson().fromJson(reader, Config.class);
        reader.close();

        this.mainGuildId = config.getMainGuildId();

        this.musicCommandChannelId = config.getMusicCommandChannelId();
        this.audioChannelId = config.getAudioChannelId();
        this.commandChannelId = config.getCommandChannelId();
        this.logChannelId = config.getLogChannelId();

        this.discordToken = config.getDiscordToken();
        this.googleAPIKey = config.getGoogleAPIKey();
        this.secretKey = config.getSecretKey();

        this.commandPrefix = config.getCommandPrefix();
        this.messageAlertLength = config.getMessageAlertLength();
        this.maxPlaylistQueue = config.getMaxPlaylistQueue();

        this.muteEnabled = config.isMuteEnabled();
        this.muteRoleName = config.getMuteRoleName();
    }

}
