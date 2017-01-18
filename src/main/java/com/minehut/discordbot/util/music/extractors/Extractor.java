package com.minehut.discordbot.util.music.extractors;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Made by the FlareBot developers
 */
public interface Extractor {
    Class<? extends AudioSourceManager> getSourceManagerClass();

    void process(String input, Player player, IMessage message, IUser user) throws Exception;

    boolean valid(String input);
}
