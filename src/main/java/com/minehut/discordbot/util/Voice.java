package com.minehut.discordbot.util;

import com.minehut.discordbot.Core;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.audio.AudioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Created by MatrixTunnel on 12/5/2016.
 */
public class Voice {

    public static void clearPlaylist(IGuild guild) {
        Core.getMusicManager().getPlayer(guild.getID()).getPlaylist().clear();
        Core.getMusicManager().getPlayer(guild.getID()).skip();
    }

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

    public static void playAudioFile(String output, IGuild guild) throws IOException, UnsupportedAudioFileException {
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(guild);

        File music = new File("C:/words/" + output + ".mp3");
        player.queue(music);
    }

}
