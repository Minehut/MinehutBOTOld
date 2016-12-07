package com.minehut.discordbot.util;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.audio.AudioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Created by MatrixTunnel on 12/5/2016.
 */
public class Voice {

    public static void setVolume(float vol, IGuild guild) { //TODO Change to %
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
        player.setVolume(vol);
    }

    public static void pause(IGuild guild) throws IOException, UnsupportedAudioFileException {
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
        if (!player.isPaused()) {
            player.setPaused(true);
        }
    }

    //TODO Add toggle pause

    public static void resume(IGuild guild) throws IOException, UnsupportedAudioFileException {
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
        if (player.isPaused()) {
            player.setPaused(false);
        }
    }

    public static void skip(IGuild guild) throws IOException, UnsupportedAudioFileException {
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
        player.skip();
    }

    public static void playAudioFile(String file, IGuild guild) throws IOException, UnsupportedAudioFileException {
        File music = new File("C:/words/" + file + ".mp3");
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);
        player.queue(music);
    }

}
