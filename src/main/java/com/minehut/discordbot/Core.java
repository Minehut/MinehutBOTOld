package com.minehut.discordbot;

import com.arsenarsen.lavaplayerbridge.PlayerManager;
import com.arsenarsen.lavaplayerbridge.libraries.LibraryFactory;
import com.arsenarsen.lavaplayerbridge.libraries.UnknownBindingException;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.commands.general.HelpCommand;
import com.minehut.discordbot.commands.general.InfoCommand;
import com.minehut.discordbot.commands.general.minehut.ServerCommand;
import com.minehut.discordbot.commands.general.minehut.StatusCommand;
import com.minehut.discordbot.commands.general.minehut.UserCommand;
import com.minehut.discordbot.commands.management.*;
import com.minehut.discordbot.commands.master.SayCommand;
import com.minehut.discordbot.commands.master.ShutdownCommand;
import com.minehut.discordbot.commands.music.*;
import com.minehut.discordbot.events.ChatEvents;
import com.minehut.discordbot.events.ServerEvents;
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
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * Created by MatrixTunnel on 11/28/2016.
 * Huge thanks to the FlareBot developers for the music support!
 */
public class Core {

    public static boolean enabled = false;

    private static Config config;
    private static IDiscordClient discord;
    private static JDA client;
    private static List<Command> commands;
    private static PlayerManager musicManager;

    public static JDA getClient() {
        return client;
    }
    public static CountDownLatch latch;
    public static Logger log = LoggerFactory.getLogger("MinehutBot");

    public static void main(String[] args) throws InterruptedException, UnknownBindingException {
        LoggerAdapter.set();

        try {
            config = new Config();
            Core.log.info("Loading config...");
            config.load();
        } catch (IOException e) {
            log.error("Config failed to load!", e);
            shutdown(false);
        }

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

            log.info("Disconnected from Discord.");
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
    }

    public static Config getConfig() {
        return config;
    }

    public static IDiscordClient getDiscord() {
        return discord;
    }

    public static List<Command> getCommands() {
        return commands;
    }

    public static PlayerManager getMusicManager() {
        return musicManager;
    }

    private void registerEvents() {
        musicManager.getPlayerCreateHooks().register(player -> player.addEventListener(new AudioEventAdapter() {
            @Override
            public void onTrackStart(AudioPlayer aplayer, AudioTrack atrack) {
                for (String id : Core.getConfig().getMusicCommandChannels()) {
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
                //Chat.removeMessage(msg);
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

    private static void registerCommands() {
        registerCommand(new ServerCommand());
        registerCommand(new StatusCommand());
        registerCommand(new UserCommand());

        registerCommand(new HelpCommand());
        registerCommand(new InfoCommand());

        registerCommand(new JoinCommand());
        registerCommand(new LeaveCommand());
        registerCommand(new MuteCommand());
        //registerCommand(new PurgeCommand());
        registerCommand(new ReconnectVoiceCommand());
        registerCommand(new ReloadCommand());
        registerCommand(new ShutdownCommand());

        registerCommand(new SayCommand());

        registerCommand(new PlayCommand());
        registerCommand(new SkipCommand());
        registerCommand(new QueueCommand());
        registerCommand(new RandomSongCommand());
        registerCommand(new NowPlayingCommand());
        registerCommand(new ToggleMusicCommand());
        registerCommand(new VolumeCommand());
    }

    public static List<Command> getCommandsByType(CommandType type) {
        return commands.stream().filter(command -> command.getType() == type).collect(Collectors.toList());
    }

    private void init() throws InterruptedException, UnknownBindingException {
        RestAction.DEFAULT_FAILURE = t -> {};

        discord = new IDiscordClient();
        latch = new CountDownLatch(1);

        try {
            try {
                client = new JDABuilder(AccountType.BOT)
                        .addEventListener(new ChatEvents(), new ServerEvents())
                        .setToken(config.getDiscordToken())
                        .setAudioSendFactory(new NativeAudioSendFactory())
                        .setGame(Game.of("loading..."))
                        .setStatus(OnlineStatus.IDLE)
                        .buildAsync();
            } catch (RateLimitedException e) {
                Thread.sleep(e.getRetryAfter());
            }

            commands = new ArrayList<>();
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
        registerCommands();
    }

    private static void registerCommand(Command command) {
        commands.add(command);
    }

}
