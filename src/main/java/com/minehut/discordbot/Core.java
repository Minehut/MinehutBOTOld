package com.minehut.discordbot;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.arsenarsen.lavaplayerbridge.PlayerManager;
import com.arsenarsen.lavaplayerbridge.libraries.LibraryFactory;
import com.arsenarsen.lavaplayerbridge.libraries.UnknownBindingException;
import com.mashape.unirest.http.Unirest;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.commands.manage.PurgeCommand;
import com.minehut.discordbot.commands.manage.ReconnectVoiceCommand;
import com.minehut.discordbot.commands.music.NowPlayingCommand;
import com.minehut.discordbot.commands.music.PlayCommand;
import com.minehut.discordbot.commands.music.QueueCommand;
import com.minehut.discordbot.commands.music.SkipCommand;
import com.minehut.discordbot.events.ChatEvents;
import com.minehut.discordbot.events.Commands;
import com.minehut.discordbot.events.ServerEvents;
import com.minehut.discordbot.events.VoiceEvents;
import com.minehut.discordbot.util.Chat;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.audio.AudioPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by MatrixTunnel on 11/28/2016.
 * Huge thanks to the FlareBot developers for the music support!
 */
public class Core {

    public static boolean enabled = false;
    public static String discordLogChatID = "253063123781550080"; //TODO Config
    public static boolean discordConnection;
    public static Logger log = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    private static IDiscordClient discord;
    private static String token = "MjUzMDY0MjQ0MTU1NjQ1OTU0.C0bfrg.CI1hZtwlhPNMgEDqq-SIB89dl8w"; //TODO Config
    private static String youTubeKey = "AIzaSyASdfSoMieeRyhoN4DZI8_k7rsDzMcQBfw"; //TODO Config
    private static String soundCloudKey = ""; //TODO Config
    private static List<Command> commands;
    private static PlayerManager musicManager;

    public static void main(String[] args) {
        log.setLevel(Level.INFO);

        HttpClient httpClient = HttpClients.custom()
                .disableCookieManagement()
                .build();
        Unirest.setHttpClient(httpClient);

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

    private static ArrayList<String> loadBadWords() {
        Scanner s;

        try {
            s = new Scanner(new File("C:/words/words.txt"));
            ArrayList<String> list = new ArrayList<>();
            while (s.hasNext()) {
                list.add(s.next());
            }
            s.close();
            return list;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void shutdown(boolean restart) {
        try {
            //broadcast("Turning myself off...");
            if (discord.getConnectedVoiceChannels().size() > 0) {
                for (IGuild guild : Core.getDiscord().getGuilds()) {
                    musicManager.getPlayer(guild.getID()).getPlaylist().clear();
                    musicManager.getPlayer(guild.getID()).skip();
                }
                //if (Core.getDiscord().getOurUser().getConnectedVoiceChannels().size() > 0) {
                //    Core.getDiscord().getOurUser().getConnectedVoiceChannels().stream().filter(channel -> channel != null).forEach(channel -> {
                //        Core.getDiscord().getVoiceChannelByID(channel.getID()).leave();
                //    });
                //}
            }

            SkipCommand.votes.clear();
            VoiceEvents.playing.forEach(Chat::removeMessage);
            //TODO Remove all messages that are waiting on task timers

            discord.logout();
            discordConnection = false;
            enabled = false;
            log.info("Disconnected from Discord");
            if (restart) {
                log.info("Restarting...");
                new ProcessBuilder("start.bat").start();
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

    public static String getSoundCloudKey() {
        return soundCloudKey;
    }

    public static PlayerManager getMusicManager() {
        return musicManager;
    }

    private void registerEvents() {
        if (discord != null) {

            discord.getDispatcher().registerListener(new ChatEvents());
            discord.getDispatcher().registerListener(new Commands());
            discord.getDispatcher().registerListener(new ServerEvents());
            discord.getDispatcher().registerListener(new VoiceEvents());
        }
    }

    public static void registerCommands() {
        //TODO registerCommand(new HelpCommand());

        registerCommand(new PurgeCommand());
        registerCommand(new ReconnectVoiceCommand());

        registerCommand(new PlayCommand());
        registerCommand(new SkipCommand());
        registerCommand(new QueueCommand());
        registerCommand(new NowPlayingCommand());
    }

    public static List<Command> getCommandsByType(CommandType type) {
        return commands.stream().filter(command -> command.getType() == type).collect(Collectors.toList());
    }

    private void init() throws UnknownBindingException {
        try {
            discord = new ClientBuilder().withToken(token).login();
            registerEvents();
            commands = new ArrayList<>();
            musicManager = PlayerManager.getPlayerManager(LibraryFactory.getLibrary(discord));

            //Discord4J.disableAudio();
            Discord4J.disableChannelWarnings();
            ChatEvents.badWords = loadBadWords();
        } catch (DiscordException e) {
            log.error("Could not log in!", e);
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

    //public static PlayerManager getMusicManager() {
        //return musicManager;
    //}
}
