package com.minehut.discordbot;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.arsenarsen.lavaplayerbridge.PlayerManager;
import com.arsenarsen.lavaplayerbridge.libraries.LibraryFactory;
import com.arsenarsen.lavaplayerbridge.libraries.UnknownBindingException;
import com.mashape.unirest.http.Unirest;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.commands.general.InfoCommand;
import com.minehut.discordbot.commands.manage.PurgeCommand;
import com.minehut.discordbot.commands.manage.ReconnectVoiceCommand;
import com.minehut.discordbot.commands.manage.ShutdownCommand;
import com.minehut.discordbot.commands.music.*;
import com.minehut.discordbot.events.ChatEvents;
import com.minehut.discordbot.events.ServerEvents;
import com.minehut.discordbot.events.VoiceEvents;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.slf4j.LoggerFactory;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by MatrixTunnel on 11/28/2016.
 * Huge thanks to the FlareBot developers for the music support!
 */
public class Core {

    public static boolean enabled = false;
    public static String discordLogChatID = "253063123781550080";
    public static boolean discordConnection;
    public static Logger log = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    private static IDiscordClient discord;
    private static String token = Secret.getDiscordToken();
    private static String youTubeKey = Secret.getYouTubeAPIKey();
    private static List<Command> commands;
    private static PlayerManager musicManager;

    public static void main(String[] args) {
        log.setLevel(Level.INFO);

        try {
            new Core().init();
        } catch (UnknownBindingException e) {
            e.printStackTrace();
        }


        do {
            String command = "_";
            try {
                command = (new BufferedReader(new InputStreamReader(System.in))).readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (command.startsWith("!")) {
                switch (command.substring(1)) {
                    case "shutdown":
                        log.info("Shutting down...");
                        shutdown(false);
                        break;
                    default:
                        log.info("Not a valid command");
                        break;
                }

            } else if (command.startsWith("|")) {
                broadcast(command.substring(1), Core.getDiscord().getChannelByID("239599059415859200"));
            }

            //broadcast(command);
        } while (enabled);

    }

    public static void shutdown(boolean restart) {
        try {
            //broadcast("Turning myself off...");
            if (discord.getConnectedVoiceChannels().size() > 0) {
                for (IGuild guild : Core.getDiscord().getGuilds()) {
                    musicManager.getPlayer(guild.getID()).getPlaylist().clear();
                    musicManager.getPlayer(guild.getID()).skip();
                }

                for (IVoiceChannel channel : Core.getDiscord().getConnectedVoiceChannels()) {
                    Core.log.info("[Guild: " + channel.getGuild().getName() + " left channel: \"" + channel.getName() + "\" (" + channel.getID() + ")]");
                    channel.leave();
                }
            }

            SkipCommand.votes.clear();
            VoiceEvents.playing.forEach(Chat::removeMessage);
            Chat.timer.cancel();
            //TODO Remove all messages that are waiting on task timers

            if (discord.isLoggedIn()) {
                discord.logout();
                log.info("Disconnected from Discord.");
            }
            discordConnection = false;
            enabled = false;
            if (restart) {
                log.info("Restarting...");
                new ProcessBuilder("/bin/bash", "run.sh").start();
            } else {
                log.info("Shut down successfully");
                Unirest.shutdown();
            }
            System.exit(0);
        } catch (DiscordException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, IChannel channel) {
        if (message != null) {
            log.info("<MinehutBOT> " + message);

            Chat.sendMessage("**[BOT]** " + message, channel);
        }
    }

    public static void broadcast(String message) {
        if (message != null) {
            log.info(message);

            Chat.sendMessage("**[BOT]** " + message, Core.getDiscord().getChannelByID(discordLogChatID));
        }
    }

    public static void broadcastRaw(String message) {
        if (message != null) {
            log.info(message);

            Chat.sendMessage(message, Core.getDiscord().getChannelByID(discordLogChatID));
        }
    }

    public static String getYoutubeKey() {
        return youTubeKey;
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
        discord.getDispatcher().registerListener(new ChatEvents());
        discord.getDispatcher().registerListener(new ServerEvents());
        discord.getDispatcher().registerListener(new VoiceEvents());

        musicManager.getPlayerCreateHooks().register(player -> player.addEventListener(new AudioEventAdapter() {

        }));

        musicManager.getPlayerCreateHooks().register(player -> player.addEventListener(new AudioEventAdapter() {
            @Override
            public void onTrackStart(AudioPlayer player, AudioTrack track) {
                for (String id : Bot.getMusicTextChannels()) {
                    if (id != null) {
                        IChannel channel = Core.getDiscord().getChannelByID(id);
                        AudioPlayer song = Core.getMusicManager().getPlayer(channel.getGuild().getID()).getPlayer();

                        if (song == player || song.getPlayingTrack() == track) {
                            IMessage msg = Chat.sendMessage("Now Playing: **" + track.getInfo().title + "**", channel);

                            SkipCommand.votes.clear();
                            VoiceEvents.playing.add(msg);
                        }
                    }
                }
            }

            @Override
            public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                //Chat.removeMessage(msg);
                SkipCommand.votes.clear();

                for (IMessage msg : VoiceEvents.playing) {
                    if (msg != null) {
                        AudioPlayer guildPlayer = Core.getMusicManager().getPlayer(msg.getGuild().getID()).getPlayer();

                        if (guildPlayer == player) {
                            Chat.removeMessage(msg);
                            VoiceEvents.playing.remove(msg);
                        }
                    }
                }
            }
        }));
    }

    public static void registerCommands() {
        //TODO registerCommand(new HelpCommand());
        registerCommand(new InfoCommand());

        registerCommand(new PurgeCommand());
        registerCommand(new ReconnectVoiceCommand());
        registerCommand(new ShutdownCommand());

        registerCommand(new PlayCommand());
        registerCommand(new SkipCommand());
        registerCommand(new QueueCommand());
        registerCommand(new NowPlayingCommand());
        registerCommand(new VolumeCommand());
    }

    public static List<Command> getCommandsByType(CommandType type) {
        return commands.stream().filter(command -> command.getType() == type).collect(Collectors.toList());
    }

    private void init() throws UnknownBindingException {
        try {
            discord = new ClientBuilder()
                    .setMaxReconnectAttempts(Integer.MAX_VALUE)
                    .withToken(token).login();

            commands = new ArrayList<>();

            //Discord4J.disableAudio();
            Discord4J.disableChannelWarnings();
            musicManager = PlayerManager.getPlayerManager(LibraryFactory.getLibrary(discord));
            registerEvents();
        } catch (DiscordException e) {
            log.error("Could not login to Discord!", e);
            shutdown(false);
        }

        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        })); // No operation STDERR. Will not do much of anything, except to filter out some Jsoup spam
    }

    private static void registerCommand(Command command) {
        commands.add(command);
    }

}
