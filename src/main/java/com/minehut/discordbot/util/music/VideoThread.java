package com.minehut.discordbot.util.music;

import com.arsenarsen.lavaplayerbridge.PlayerManager;
import com.minehut.discordbot.MinehutBot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.music.extractors.Extractor;
import com.minehut.discordbot.util.music.extractors.SoundCloudExtractor;
import com.minehut.discordbot.util.music.extractors.YouTubeExtractor;
import com.minehut.discordbot.util.music.extractors.YouTubeSearchExtractor;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Made by the FlareBot developers
 * Few small changes by MatrixTunnel
 */
public class VideoThread extends Thread {

    private static PlayerManager manager;
    private static final List<Class<? extends Extractor>> extractors = Arrays.asList(YouTubeExtractor.class, SoundCloudExtractor.class);
    private static final Set<Class<? extends AudioSourceManager>> managers = new HashSet<>();
    public static final ThreadGroup VIDEO_THREADS = new ThreadGroup("Video Threads");

    private UserClient client;
    private TextChannel channel;
    private String url;
    private Extractor extractor;

    private VideoThread() {
        if (manager == null)
            manager = MinehutBot.get().getMusicManager();
        setName("Video Thread " + VIDEO_THREADS.activeCount());
    }

    @Override
    public void run() {
        channel.sendMessage("Processing...").queue(message -> {
            Chat.removeMessage(message, 120);
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
                    Chat.editMessage(client.getUser().getAsMention() + " Your request could not be processed. Please try again", message, 10);
                    return;
                }
                if (managers.add(extractor.getSourceManagerClass()))
                    manager.getManager().registerSourceManager(extractor.getSourceManagerClass().newInstance());
                extractor.process(url, manager.getPlayer(channel.getGuild().getId()), message, client);
            } catch (Exception e) {
                MinehutBot.log.error("Could not init extractor for '" + url + "'", e);
                Chat.editMessage(client.getUser().getAsMention() + " Something went wrong while processing your command. Please try again later", message, 10);
            }
        });
    }

    @Override
    public void start() {
        if (url == null)
            throw new IllegalStateException("URL Was not set!");
        super.start();
    }

    public static VideoThread getThread(String url, TextChannel channel, UserClient client) {
        VideoThread thread = new VideoThread();
        thread.url = url;
        thread.channel = channel;
        thread.client = client;
        return thread;
    }

    public static VideoThread getSearchThread(String term, TextChannel channel, UserClient client) {
        VideoThread thread = new VideoThread();
        thread.url = term;
        thread.channel = channel;
        thread.client = client;
        thread.extractor = new YouTubeSearchExtractor();
        return thread;
    }
}
