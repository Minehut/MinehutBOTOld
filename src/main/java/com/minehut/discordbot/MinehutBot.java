package com.minehut.discordbot;

import com.arsenarsen.lavaplayerbridge.PlayerManager;
import com.arsenarsen.lavaplayerbridge.libraries.LibraryFactory;
import com.arsenarsen.lavaplayerbridge.libraries.UnknownBindingException;
import com.minehut.discordbot.commands.CommandHandler;
import com.minehut.discordbot.commands.music.SkipCommand;
import com.minehut.discordbot.events.ChatEvents;
import com.minehut.discordbot.events.ServerEvents;
import com.minehut.discordbot.events.VoiceEvents;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.Config;
import com.minehut.discordbot.util.UserManager;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by MatrixTunnel on 11/28/2016.
 */
@Getter
public class MinehutBot {

    private static MinehutBot instance;

    private JDA discordClient;
    private Config config;
    private UserManager userManager;

    private PlayerManager musicManager;
    private OkHttpClient httpClient;

    public static Long startMillis;

    public static Logger log = LoggerFactory.getLogger("MinehutBot");
    public static boolean enabled = false;

    public static void main(String[] args) {
        startMillis = System.currentTimeMillis();

        try {
            (instance = new MinehutBot()).init();
        } catch (InterruptedException | UnknownBindingException e) {
            e.printStackTrace();
            instance.shutdown(false);
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            switch (scanner.nextLine()) {
                case "stop":
                case "exit":
                case "halt":
                case "die":
                    instance.shutdown(false);
                    break;
                case "restart":
                case "reincarnate":
                    instance.shutdown(true);
                    break;
            }
        }
    }

    public void shutdown(boolean restart) {
        if (enabled) {
            enabled = false;

            boolean wasReady = Bot.isReady();

            if (wasReady) {
                discordClient.getPresence().setGame(Game.of(Game.GameType.DEFAULT, "shutting down..."));
                discordClient.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
            }

            try {
                userManager.save(userManager.getUserJson());
                log.info("User data saved!");
            } catch (IOException e) {
                log.warn("Failed to save user data!", e);
            }

            try {
                if (wasReady) {
                    if (!Bot.nowPlaying.isEmpty()) {
                        if (!SkipCommand.votes.isEmpty()) {
                            SkipCommand.votes.clear();
                        }
                    }

                    //TODO Remove all messages that are waiting on task timers

                    if (Bot.getConnectedVoiceChannel() != null) {
                        musicManager.getPlayer(Bot.getMainGuild().getId()).getPlaylist().clear();
                        //musicManager.getPlayer(Bot.getMainGuild().getId()).skip();

                        Bot.nowPlaying.forEach(Chat::removeMessage);
                    }
                }

                log.info("Cleaning things up...");

                discordClient.shutdown();

                log.info("Client shutdown.");

                if (restart) {
                    log.info("Restarting...");
                    new ProcessBuilder("/bin/bash", "run.sh").start();
                }

                System.exit(0);
            } catch (IOException e) {
                log.warn("Could not start restart process!", e);
                System.exit(1);
            }
        } else {
            log.warn("Client never initialized!");
            System.exit(1);
        }
    }

    private void registerMusicEvents() {
        musicManager.getPlayerCreateHooks().register(player -> player.addEventListener(new AudioEventAdapter() {
            @Override
            public void onTrackStart(AudioPlayer aplayer, AudioTrack atrack) {
                String musicCommandChannelId = config.getMusicCommandChannelId();

                if (musicCommandChannelId != null) {
                    TextChannel channel = discordClient.getTextChannelById(musicCommandChannelId);

                    if (channel != null) {
                        AudioPlayer song = getMusicManager().getPlayer(channel.getGuild().getId()).getPlayer();
                        User user = discordClient.getUserById(player.getPlayingTrack().getMeta().get("requester").toString());

                        if (song == aplayer || song.getPlayingTrack() == atrack) {
                            EmbedBuilder embed = Chat.getEmbed();

                            embed.addField("**Now playing** - " + (atrack instanceof YoutubeAudioTrack ? "YouTube" : atrack instanceof SoundCloudAudioTrack ? "SoundCloud" : "???"),
                                    "**[" + atrack.getInfo().title + "](" + atrack.getInfo().uri + ")** `[" + Bot.millisToTime(song.getPlayingTrack().getDuration(), false) + "]`", true)
                                    .setImage(atrack instanceof YoutubeAudioTrack ? "https://img.youtube.com/vi/" + song.getPlayingTrack().getIdentifier() + "/mqdefault.jpg" :
                                              atrack instanceof SoundCloudAudioTrack ? "https://cdn.discordapp.com/attachments/233737506955329538/290302284381028352/soundcloud_icon.png" : null)
                                    .setFooter("Queued by: @" + (user != null ? Chat.getFullName(user) : "null"), null)
                                    .setColor(atrack instanceof YoutubeAudioTrack ? Chat.CUSTOM_GREEN : atrack instanceof SoundCloudAudioTrack ? Chat.CUSTOM_DARK_ORANGE : Chat.CUSTOM_GREEN);

                            channel.sendMessage(new MessageBuilder().setEmbed(embed.build()).build()).queue(message -> {
                                SkipCommand.votes.clear();
                                Bot.nowPlaying.add(message);
                            });
                        }
                    }
                }
            }

            @Override
            public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                SkipCommand.votes.clear();

                List<Message> toRemove = new ArrayList<>();

                if (!Bot.nowPlaying.isEmpty()) {
                    Bot.nowPlaying.forEach(message -> {
                        AudioPlayer guildPlayer = getMusicManager().getPlayer(message.getGuild().getId()).getPlayer();

                        if (guildPlayer == player) {
                            toRemove.add(message);

                            if (message.getChannel().getMessageById(message.getId()) != null)
                                Chat.removeMessage(message);
                        }
                    });
                }

                Bot.nowPlaying.removeAll(toRemove);
                toRemove.clear();
            }
        }));
    }

    private void loadStorage() {
        try {
            File settings = new File(Config.FILE_NAME);
            if (!settings.exists()) {
                settings.createNewFile();
                new Config().save(new Config());
                MinehutBot.log.info("New settings.json generated! Please enter settings and try again");
                shutdown(false);
                return;
            }

            File users = new File(UserManager.FILE_NAME);
            if (!users.exists()) {
                users.createNewFile();
                userManager.save("[{\"id\":\"12345\"}]");
                MinehutBot.log.info("New users.json generated! Dummy file added");
            }

            config = new Config();
            MinehutBot.log.info("Loading config...");
            config.load();

            if (config.getDiscordToken().isEmpty()) {
                log.warn("Discord token is empty. Please fill out the config and try again");
                shutdown(false);
                return;
            } else if (config.getMainGuildId().isEmpty()) {
                log.warn("Main guild id empty. Please fill out the config and try again");
                shutdown(false);
                return;
            } else if (config.getGoogleAPIKey().isEmpty()) {
                log.warn("Google api key is empty. Music functionality disabled");
            }

            MinehutBot.log.info("Loading users...");
            userManager.load();
        } catch (IOException e) {
            log.error("Failed loading data!", e);
            shutdown(false);
        }
    }

    private void init() throws InterruptedException, UnknownBindingException {
        userManager = new UserManager();

        loadStorage();

        RestAction.DEFAULT_FAILURE = t -> {};

        try {
            discordClient = new JDABuilder(AccountType.BOT)
                    .addEventListener(new ChatEvents(), new ServerEvents(), new VoiceEvents())
                    .setToken(config.getDiscordToken())
                    .setAudioSendFactory(new NativeAudioSendFactory())
                    .setCorePoolSize(8)
                    .setGame(Game.of(Game.GameType.DEFAULT, "loading..."))
                    .setStatus(OnlineStatus.IDLE)
                    .buildAsync();

            musicManager = PlayerManager.getPlayerManager(LibraryFactory.getLibrary(discordClient));
            registerMusicEvents();
        } catch (Exception e) {
            log.error("Could not log in!", e);
            Thread.sleep(500);
            shutdown(false);
        }

        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        })); // No operation STDERR. Will not do much of anything, except to filter out some Jsoup spam

        CommandHandler.registerCommands();
        httpClient = new OkHttpClient.Builder().build();
    }

    public static MinehutBot get() {
        return instance;
    }
}
