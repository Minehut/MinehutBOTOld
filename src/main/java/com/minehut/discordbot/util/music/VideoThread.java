package com.minehut.discordbot.util.music;

import com.arsenarsen.lavaplayerbridge.PlayerManager;
import com.minehut.discordbot.Core;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.music.extractors.Extractor;
import com.minehut.discordbot.util.music.extractors.YouTubeExtractor;
import com.minehut.discordbot.util.music.extractors.YouTubeSearchExtractor;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Made by the FlareBot developers
 * Few small changes by MatrixTunnel
 */
public class VideoThread extends Thread {

    private static PlayerManager manager;
    private static final List<Class<? extends Extractor>> extractors = Collections.singletonList(YouTubeExtractor.class);
    private static final Set<Class<? extends AudioSourceManager>> managers = new HashSet<>();
    public static final ThreadGroup VIDEO_THREADS = new ThreadGroup("Video Threads");
    private User user;
    private TextChannel channel;
    private String url;
    private Extractor extractor;

    private VideoThread() {
        if (manager == null)
            manager = Core.getMusicManager();
        setName("Video Thread " + VIDEO_THREADS.activeCount());
    }

    @Override
    public void run() {
        Message message = Chat.sendMessage("Processing...", channel, 120);
        try {
            if (extractor == null)
                for (Class<? extends Extractor> clazz : extractors) {
                    Extractor extractor = clazz.newInstance();
                    if (!extractor.valid(url))
                        continue;
                    this.extractor = extractor;
                    break;
                }
            if (extractor == null) {
                Chat.editMessage(message, "Could not find a way to process that..", 30);
                return;
            }
            if (managers.add(extractor.getSourceManagerClass()))
                manager.getManager().registerSourceManager(extractor.getSourceManagerClass().newInstance());
            extractor.process(url, manager.getPlayer(channel.getGuild().getId()), message, user);
        } catch (Exception e) {
            Core.log.error("Could not init extractor for '{}'".replace("{}", url), e);
            Chat.editMessage(message, "Something went wrong! Issue logged", 30);
        }
    }

    @Override
    public void start() {
        if (url == null)
            throw new IllegalStateException("URL Was not set!");
        super.start();
    }

    public static VideoThread getThread(String url, TextChannel channel, User user) {
        VideoThread thread = new VideoThread();
        thread.url = url;
        thread.channel = channel;
        thread.user = user;
        return thread;
    }

    public static VideoThread getSearchThread(String term, TextChannel channel, User user) {
        VideoThread thread = new VideoThread();
        thread.url = term;
        thread.channel = channel;
        thread.user = user;
        thread.extractor = new YouTubeSearchExtractor();
        return thread;
    }
}
