package com.minehut.discordbot;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.minehut.discordbot.events.ChatEvents;
import com.minehut.discordbot.events.Commands;
import com.minehut.discordbot.events.ServerEvents;
import com.minehut.discordbot.util.Chat;
import org.slf4j.LoggerFactory;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by MatrixTunnel on 11/28/2016.
 */
public class Core {

    public static boolean enabled = false;
    public static String discordLogChatID = "253063123781550080"; //TODO Config
    public static boolean discordConnection;
    public static Discord4J.Discord4JLogger log = new Discord4J.Discord4JLogger("BOT");
    private static IDiscordClient discord = null;
    private static ClientBuilder clientBuilder = new ClientBuilder();
    private static String token = "MjUzMDY0MjQ0MTU1NjQ1OTU0.Cx9tlQ.kSuH4KOZ0DqE_1KXPloogbKvJQ0"; //TODO Config

    private static IDiscordClient getClient(String token) throws DiscordException {
        //clientBuilder.withShards(2);
        return clientBuilder.withToken(token).login();
    }

    public static void main(String[] args) throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        //Discord4J.disableAudio();
        ChatEvents.badWords = loadBadWords();

        discord = getClient(token);
        registerEvents();

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
                        shutdown();
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

    public static void shutdown() {
        try {
            //broadcast("Turning myself off...");

            discord.logout();
            discordConnection = false;
            enabled = false;
            log.info("Disconnected from Discord");
        } catch (DiscordException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, IChannel channel) {
        if (message != null) {
            log.info("<MinehutBOT> " + message);

            Chat.sendDiscordMessage("**[BOT]** " + message, channel);
        }
    }

    public static void broadcast(String message) {
        if (message != null) {
            log.info(message);

            Chat.sendDiscordMessage("**[BOT]** " + message);
        }
    }

    public static void broadcastRaw(String message) {
        if (message != null) {
            log.info(message);

            Chat.sendDiscordMessage(message);
        }
    }

    private static void registerEvents() {
        if (discord != null) {

            discord.getDispatcher().registerListener(new ServerEvents());
            discord.getDispatcher().registerListener(new ChatEvents());
            discord.getDispatcher().registerListener(new Commands());
        }
    }

    public static IDiscordClient getDiscord() {
        return discord;
    }

}
