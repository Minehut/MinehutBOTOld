package com.minehut.discordbot;

import com.arsenarsen.lavaplayerbridge.PlayerManager;
import com.arsenarsen.lavaplayerbridge.libraries.LibraryFactory;
import com.arsenarsen.lavaplayerbridge.libraries.UnknownBindingException;
import com.minehut.discordbot.commands.CommandHandler;
import com.minehut.discordbot.commands.music.SkipCommand;
import com.minehut.discordbot.events.ChatEvents;
import com.minehut.discordbot.events.ServerEvents;
import com.minehut.discordbot.events.VoiceEvents;
import com.minehut.discordbot.util.*;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Created by MatrixTunnel on 11/28/2016.
 * Huge thanks to the FlareBot developers for the music support!
 */
public class Core {

    public static boolean enabled = false;

    private static Config config;
    private static IDiscordClient discord;
    private static JDA client;
    private static PlayerManager musicManager;
    private static CommandHandler commandHandler;

    public static JDA getClient() {
        return client;
    }
    public static CountDownLatch latch;
    public static Logger log = LoggerFactory.getLogger("MinehutBot");

    public static void main(String[] args) throws InterruptedException, UnknownBindingException {
        LoggerAdapter.set();

        try {
            new GuildSettings().load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadConfig();

        new Core().init();
        Scanner scanner = new Scanner(System.in);

        do {
            try {
                if (scanner.next().equalsIgnoreCase("exit")) {
                    shutdown(false);
                } else if (scanner.next().equalsIgnoreCase("restart")) {
                    shutdown(true);
                }
            } catch (NoSuchElementException ignored) {
            }
        } while (enabled);
    }

    public static void shutdown(boolean restart) {
        if (enabled) {
            enabled = false;

            Core.getClient().getPresence().setGame(Game.of("shutting down..."));
            Core.getClient().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);

            try {
                if (!Bot.nowPlaying.isEmpty()) {
                    if (!SkipCommand.votes.isEmpty()) {
                        SkipCommand.votes.clear();
                    }
                }

                //TODO Remove all messages that are waiting on task timers

                if (discord.getConnectedVoiceChannels().size() > 0) {
                    for (VoiceChannel voiceChannel : discord.getConnectedVoiceChannels()) {
                        Core.getMusicManager().getPlayer(voiceChannel.getGuild().getId()).getPlaylist().clear();
                        Core.getMusicManager().getPlayer(voiceChannel.getGuild().getId()).skip();
                    }

                    Bot.nowPlaying.forEach(Chat::removeMessage);
                }

                client.removeEventListener(new ChatEvents(), new ServerEvents());

                if (restart) {
                    log.info("Restarting...");
                    new ProcessBuilder("/bin/bash", "run.sh").start();
                } else {
                    log.info("Cleaning things up...");
                }

                Thread.sleep(1500);

                client.shutdown();
                log.info("Client shutdown");
                System.exit(0);
            } catch (IOException e) {
                log.warn("Could not start restart process!", e);
            } catch (InterruptedException e) {
                log.warn("Could not pause shutdown thread!", e);
                e.printStackTrace();
            }
        } else {
            System.exit(0);
        }
    }

    public static Config getConfig() {
        return config;
    }

    public static IDiscordClient getDiscord() {
        return discord;
    }

    public static PlayerManager getMusicManager() {
        return musicManager;
    }

    private void registerEvents() {
        musicManager.getPlayerCreateHooks().register(player -> player.addEventListener(new AudioEventAdapter() {
            @Override
            public void onTrackStart(AudioPlayer aplayer, AudioTrack atrack) {
                for (String id : GuildSettings.getMusicCommandChannels()) {
                    if (id != null) {
                        TextChannel channel = client.getTextChannelById(id);
                        if (channel != null) {
                            AudioPlayer song = getMusicManager().getPlayer(channel.getGuild().getId()).getPlayer();
                            User user = Core.getClient().getUserById(player.getPlayingTrack().getMeta().get("requester").toString());

                            if (song == aplayer || song.getPlayingTrack() == atrack) {
                                EmbedBuilder embed = Chat.getEmbed();

                                if (atrack instanceof YoutubeAudioTrack) {
                                    embed.addField("**Now playing** - YouTube", "**[" + atrack.getInfo().title + "](" + atrack.getInfo().uri + ")** " +
                                            "`[" + Bot.millisToTime(song.getPlayingTrack().getDuration(), false) + "]`", true)
                                            .setImage("https://img.youtube.com/vi/" + song.getPlayingTrack().getIdentifier() + "/mqdefault.jpg")
                                            .setFooter("Queued by: @" + Chat.getFullName(user), null)
                                            .setColor(Chat.CUSTOM_GREEN);
                                } else if (atrack instanceof SoundCloudAudioTrack) {
                                    embed.addField("**Now playing** - SoundCloud", "**[" + atrack.getInfo().title + "](" + atrack.getInfo().uri + ")** " +
                                            "`[" + Bot.millisToTime(song.getPlayingTrack().getDuration(), false) + "]`", true)
                                            .setImage("https://cdn.discordapp.com/attachments/233737506955329538/290302284381028352/soundcloud_icon.png") //I made this -Matrix
                                            .setFooter("Queued by: @" + Chat.getFullName(user), null)
                                            .setColor(Chat.CUSTOM_DARK_ORANGE);
                                } else {
                                    embed.addField("**Now playing** - ???", "**[" + atrack.getInfo().title + "](" + atrack.getInfo().uri + ")** " +
                                            "`[" + Bot.millisToTime(song.getPlayingTrack().getDuration(), false) + "]`", true)
                                            .setFooter("Queued by: @" + Chat.getFullName(user), null)
                                            .setColor(Chat.CUSTOM_GREEN);
                                }

                                Message msg = channel.sendMessage(new MessageBuilder().setEmbed(embed.build()).build()).complete();

                                SkipCommand.votes.clear();
                                Bot.nowPlaying.add(msg);
                            }
                        }
                    }
                }
            }

            @Override
            public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                SkipCommand.votes.clear();

                for (Message msg : Bot.nowPlaying) {
                    if (Bot.nowPlaying.isEmpty()) {
                        break;
                    }

                    if (msg != null) {
                        AudioPlayer guildPlayer = Core.getMusicManager().getPlayer(msg.getGuild().getId()).getPlayer();

                        if (guildPlayer == player) {
                            Chat.removeMessage(msg);
                            Bot.nowPlaying.remove(msg);
                            break;
                        }
                    }
                }
            }
        }));
    }

    private static void loadConfig() {
        try {
            File settings = new File(Config.FILE_NAME);
            if (!settings.exists()) {
                settings.createNewFile();
                Config.save(new Config());
                Core.log.info("New settings.json generated! Please enter the settings and try again");
                shutdown(false);
            }

            config = new Config();
            Core.log.info("Loading config...");
            config.load();

            File guildsSettings = new File(GuildSettings.FILE_NAME);
            if (!guildsSettings.exists()) {
                guildsSettings.createNewFile();
                Core.log.info("New quilds.json generated! Please enter the settings and try again");
                shutdown(false);
            }
        } catch (IOException e) {
            log.error("Failed loading config(s)!", e);
            shutdown(false);
        }
    }

    private void init() throws InterruptedException, UnknownBindingException {
        RestAction.DEFAULT_FAILURE = t -> {};

        discord = new IDiscordClient(); //TODO Move into Bot class
        latch = new CountDownLatch(1);
        commandHandler = new CommandHandler();

        try {
            try {
                client = new JDABuilder(AccountType.BOT)
                        .addEventListener(new ChatEvents(), new ServerEvents(), new VoiceEvents(), commandHandler)
                        .setToken(config.getDiscordToken())
                        .setAudioSendFactory(new NativeAudioSendFactory())
                        .setGame(Game.of("loading..."))
                        .setStatus(OnlineStatus.IDLE)
                        .buildAsync();
            } catch (RateLimitedException e) {
                Thread.sleep(e.getRetryAfter());
            }

            musicManager = PlayerManager.getPlayerManager(LibraryFactory.getLibrary(client));
            registerEvents();
        } catch (LoginException e) {
            log.error("Could not login to Discord!", e);
            Thread.sleep(500);
            shutdown(false);
        }
        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        })); // No operation STDERR. Will not do much of anything, except to filter out some Jsoup spam

        latch.await();
        commandHandler.registerCommands();
    }

}
