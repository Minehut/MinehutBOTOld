package com.minehut.discordbot.events;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.minehut.discordbot.Core;
import com.minehut.discordbot.util.Chat;
import com.sun.management.OperatingSystemMXBean;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * Created by MatrixTunnel on 12/6/2016.
 */
public class Commands {

    private Runtime runtime = Runtime.getRuntime();

    private String getMb(long bytes) {
        return (bytes / 1024 / 1024) + "MB";
    }

    //TODO Remove this mess

    @Deprecated
    @EventSubscriber
    public void handle(sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent event) throws MissingPermissionsException, IOException, UnsupportedAudioFileException, DiscordException, RateLimitException, UnirestException {
        if (!event.getClient().isReady()) return;
        if (event.getMessage().getChannel() instanceof IPrivateChannel) return;

        IMessage message = event.getMessage();
        IUser sender = event.getMessage().getAuthor();
        IGuild guild = event.getClient().getGuildByID(event.getMessage().getGuild().getID());

        if (message.getContent() == null || !message.getContent().startsWith("%") || !message.getChannel().getID().equals("250849094208192512")) return;

        if (!sender.getID().equals("118088732753526784")) { //TODO Let roles do commands too
            return;
        }

        String command = message.getContent().replaceFirst("%", "");
        String[] args = command.split(" ");
        EmbedBuilder embed = new EmbedBuilder().withColor(new Color(66, 173, 244));

        //Chat.logRemove = false;
        Chat.setAutoDelete(event.getMessage(), 10);
        Core.log.info(sender.getName() + " did the command \"" + message.getContent() + "\"");

        switch (args[0]) {
            case "info":
                embed.withAuthorIcon(event.getClient().getApplicationIconURL()).withAuthorName(event.getClient().getApplicationName()).withAuthorUrl("https://www.minehut.com");
                embed.appendField("Memory Usage: ", getMb(runtime.totalMemory() - runtime.freeMemory()), true);
                embed.appendField("Memory Free: ", getMb(runtime.freeMemory()), true);
                embed.appendField("Total Memory: ", getMb(runtime.totalMemory()), true);
                embed.appendField("Total threads: ", String.valueOf(Thread.getAllStackTraces().size()), true);
                embed.appendField("CPU Usage: ", ((int) (ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getSystemCpuLoad() * 10000)) / 100f + "%", true);
                embed.appendField("Discord4J Version: ", Discord4J.VERSION, true);
                embed.withFooterText("System time: " + new Date().toString());

                Chat.sendMessage(embed, message.getChannel(), 30);
                break;
            case "intellijisbetter":
                /*
                *   Original command message from Baristron bot
                *   https://github.com/chrislo27/Baristron
                */
                String mess =
                        "__Reasons why IntelliJ Idea is better than Eclipse (by dec)__\n```Markdown\n" +
                                "1. The git vcs integration is far superior to eclipses clunky mess, it abstracts away all " +
                                "the stuff you don't really need to know like staging files and just displays the " +
                                "git status of a file with easy to remember colours, doesn't natively support " +
                                "gitignore but a simple plugin you can download in under five mins sorts that\n" +

                                "2. Eclipse's dark themes are laughably bad, intellij has many theme options but the" +
                                " main two are regular white which is okay if you like being blinded and darcula " +
                                "which is a light grey theme that's easy on the eyes and doesn't give you " +
                                "headaches if you're coding for ages\n" +

                                "3. It has native maven and gradle and other build config tool integrations (even " +
                                "supports sbt for scala), even the pom files have full proper linting and code " +
                                "suggestions\n" +

                                "4. Intellij uses \"inspections\" which are totally user configurable to suggest " +
                                "how you could improve your code by collapsing SAMs into lambdas, suggesting " +
                                "change of scope for variables etc\n" +

                                "5. The program itself is far more lightweight in terms of both memory usage and " +
                                "hdd usage and it's very easy to manage multiple entirely different projects at " +
                                "the same time.\n" +

                                "6. It has a full integrated plugin ecosystem which is kept well updated and can be" +
                                " accessed via file - settings - plugins where you can find anything from making " +
                                ".gitignore show ignored files as gray to plugins that support entire languages " +
                                "for linting and inspections.\n" +

                                "7. This bot was made in IntelliJ\n" +

                                "8. It's also updated frequently and jetbrains are pretty k00l guys```";
                Chat.sendMessage(mess, event.getMessage().getChannel());
                break;
            //TODO Temp private channels
            /*
            case "channel":
                try {
                    IVoiceChannel v = event.getMessage().getGuild().createVoiceChannel(args[1]);
                    v.changeUserLimit(Integer.valueOf(args[2]));
                } catch (DiscordException | RateLimitException e) {
                    e.printStackTrace();
                }
                break;
                */
        }

    }

}
