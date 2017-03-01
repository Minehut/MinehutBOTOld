package com.minehut.discordbot;

import com.arsenarsen.lavaplayerbridge.PlayerManager;
import com.arsenarsen.lavaplayerbridge.libraries.LibraryFactory;
import com.arsenarsen.lavaplayerbridge.libraries.UnknownBindingException;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.commands.general.InfoCommand;
import com.minehut.discordbot.commands.manage.*;
import com.minehut.discordbot.commands.music.*;
import com.minehut.discordbot.events.ChatEvents;
import com.minehut.discordbot.events.ServerEvents;
import com.minehut.discordbot.events.VoiceEvents;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.Config;
import com.minehut.discordbot.util.IDiscordClient;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * Created by MatrixTunnel on 11/28/2016.
 * Huge thanks to the FlareBot developers for the music support!
 */
public class Core {

    public static boolean enabled = false;
    public static String discordLogChatID = "253063123781550080";

    private static Config config;
    private static IDiscordClient discord;
    private static JDA client;
    private static List<Command> commands;
    private static PlayerManager musicManager;

    private static final Map<String, Logger> LOGGERS = new ConcurrentHashMap<>();
    public static final Logger log = getLog(Core.class);
    private static Logger getLog(String name) {
        return LOGGERS.computeIfAbsent(name, LoggerFactory::getLogger);
    }
    private static Logger getLog(Class<?> clazz) {
        return getLog(clazz.getName());
    }

    public static JDA getClient() {
        return client;
    }
    public static CountDownLatch latch;

    public static void main(String[] args) throws InterruptedException, UnknownBindingException {
        RestAction.DEFAULT_FAILURE = t -> log.error("RestAction failed!", t);
        SimpleLog.LEVEL = SimpleLog.Level.OFF;
        SimpleLog.addListener(new SimpleLog.LogListener() {
            @Override
            public void onLog(SimpleLog log, SimpleLog.Level logLevel, Object message) {
                switch (logLevel) {
                    case ALL:
                    case INFO:
                        getLog(log.name).info(String.valueOf(message));
                        break;
                    case FATAL:
                        getLog(log.name).error(String.valueOf(message));
                        break;
                    case WARNING:
                        getLog(log.name).warn(String.valueOf(message));
                        break;
                    case DEBUG:
                        getLog(log.name).debug(String.valueOf(message));
                        break;
                    case TRACE:
                        getLog(log.name).trace(String.valueOf(message));
                        break;
                    case OFF:
                        break;
                }
            }

            @Override
            public void onError(SimpleLog log, Throwable err) {

            }
        });

        Thread.setDefaultUncaughtExceptionHandler(((t, e) -> log.error("Uncaught exception in thread " + t, e)));
        Thread.currentThread().setUncaughtExceptionHandler(((t, e) -> log.error("Uncaught exception in thread " + t, e)));

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
        //log.info("Turning myself off...");
        try {
            if (!VoiceEvents.playing.isEmpty()) {
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

                VoiceEvents.playing.forEach(Chat::removeMessage);
            }

            log.info("Disconnected from Discord.");
            if (restart) {
                log.info("Restarting...");
                new ProcessBuilder("/bin/bash", "run.sh").start();
            } else {
                log.info("Shut down successfully");
            }

            System.exit(0);
        } catch (IOException e) {
            log.info("Could not start restart process!");
        }
    }

    public static void broadcast(String message, MessageChannel channel) {
        if (message != null) {
            log.info("<MinehutBOT> " + message);

            Chat.sendMessage("**[BOT]** " + message, channel);
        }
    }

    public static void broadcast(String message) {
        if (message != null) {
            log.info(message);

            Chat.sendMessage("**[BOT]** " + message, client.getTextChannelById(discordLogChatID));
        }
    }

    public static void broadcastRaw(String message) {
        if (message != null) {
            log.info(message);

            Chat.sendMessage(message, client.getTextChannelById(discordLogChatID));
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
                            User user = Core.getDiscord().getUserByID(player.getPlayingTrack().getMeta().get("requester").toString());

                            if (song == aplayer || song.getPlayingTrack() == atrack) {
                                Message msg = Chat.sendMessage(Chat.getEmbed().addField("**Now playing**", "**[" + atrack.getInfo().title + "](https://www.youtube.com/watch?v=" +
                                                song.getPlayingTrack().getIdentifier() + ")** `[" + Bot.millisToString(song.getPlayingTrack().getDuration()) + "]`", true)
                                        .setImage("https://img.youtube.com/vi/" + atrack.getInfo().identifier + "/mqdefault.jpg")
                                        .setFooter("Queued by: @" + Chat.getFullName(user), null)
                                        .setColor(Chat.CUSTOM_GREEN), channel);

                                SkipCommand.votes.clear();
                                VoiceEvents.playing.add(msg);
                            }
                        }
                    }
                }
            }

            @Override
            public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                //Chat.removeMessage(msg);
                SkipCommand.votes.clear();

                for (Message msg : VoiceEvents.playing) {
                    if (VoiceEvents.playing.isEmpty()) {
                        break;
                    }

                    if (msg != null) {
                        AudioPlayer guildPlayer = Core.getMusicManager().getPlayer(msg.getGuild().getId()).getPlayer();

                        if (guildPlayer == player) {
                            Chat.removeMessage(msg);
                            VoiceEvents.playing.remove(msg);
                            break;
                        }
                    }
                }
            }
        }));
    }

    public static void registerCommands() {
        //TODO registerCommand(new HelpCommand());
        registerCommand(new InfoCommand());

        registerCommand(new JoinCommand());
        registerCommand(new MuteCommand());
        //registerCommand(new PurgeCommand());
        registerCommand(new ReconnectVoiceCommand());
        registerCommand(new ReloadCommand());
        registerCommand(new ShutdownCommand());

        registerCommand(new PlayCommand());
        registerCommand(new SkipCommand());
        registerCommand(new QueueCommand());
        registerCommand(new RandomSongCommand());
        registerCommand(new NowPlayingCommand());
        registerCommand(new VolumeCommand());
    }

    public static List<Command> getCommandsByType(CommandType type) {
        return commands.stream().filter(command -> command.getType() == type).collect(Collectors.toList());
    }

    private void init() throws InterruptedException, UnknownBindingException {
        discord = new IDiscordClient();
        latch = new CountDownLatch(1);

        try {
            try {
                client = new JDABuilder(AccountType.BOT)
                        .addListener(new ChatEvents(), new ServerEvents(), new VoiceEvents())
                        .setToken(config.getDiscordToken())
                        .setAudioSendFactory(new NativeAudioSendFactory())
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
