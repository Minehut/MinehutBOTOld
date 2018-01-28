package com.minehut.discordbot.commands.general.minehut;

import com.minehut.discordbot.MinehutBot;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.exceptions.CommandException;
import com.minehut.discordbot.util.music.VideoThread;
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MatrixTunnel on 4/14/2017.
 * Matcher code/regex made by MeowingTwurtle.
 */
public class StatusCommand extends Command {

    private MinehutBot bot = MinehutBot.get();

    private List<String> subCommands = Arrays.asList("bot", "servers");

    public StatusCommand() {
        super(CommandType.GENERAL, "<bot|servers>", "status");
    }

    @Override
    public boolean onCommand(UserClient sender, Guild guild, TextChannel channel, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        if (args.length == 1) {
            if (!subCommands.contains(args[0])) return false;

            Member member = guild.getMember(sender.getUser());
            EmbedBuilder embed = Chat.getEmbed();

            Message mainMsg = channel.sendMessage(new MessageBuilder().setEmbed(embed.addField("Gathering Information...", "This may take a few moments", true)
                    .setFooter("Requested by @" + Chat.getFullName(sender.getUser()), null).setColor(Chat.CUSTOM_ORANGE).build()).build()).complete();
            Chat.removeMessage(mainMsg, 30);


            switch (args[0]) {
                case "bot":
                    Runtime runtime = Runtime.getRuntime();
                    Chat.editMessage(Chat.respondMessage(member, embed.clearFields()
                            .setAuthor(bot.getDiscordClient().getSelfUser().getName() + " Bot Info", "https://minehut.com", bot.getDiscordClient().getSelfUser().getEffectiveAvatarUrl())
                            .addField("Memory Usage", Bot.getMb(runtime.totalMemory() - runtime.freeMemory()), true)
                            .addField("Memory Free", Bot.getMb(runtime.freeMemory()), true)
                            .addField("Total Memory", Bot.getMb(runtime.totalMemory()), true)
                            .addField("Video Threads", String.valueOf(VideoThread.VIDEO_THREADS.activeCount()), true)
                            .addField("Total Threads", String.valueOf(Thread.getAllStackTraces().size()), true)
                            .addField("Total Cores", String.valueOf(runtime.availableProcessors()), true)
                            .addField("CPU Usage", ((int) (ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getSystemCpuLoad() * 10000)) / 100f + "%", true)
                            .addField("Lavaplayer Version", PlayerLibrary.VERSION, true)
                            .addField("JDA Version", JDAInfo.VERSION, true)
                            .addField("Uptime", Bot.millisToTime(System.currentTimeMillis() - MinehutBot.startMillis, true), true)
                            .setColor(Chat.CUSTOM_GREEN)
                            .setFooter("Bot made by @" + Chat.getFullName(bot.getDiscordClient().getUserById(118088732753526784L)), //Credit where credit is due ¯\_(ツ)_/¯
                                    bot.getDiscordClient().getUserById(118088732753526784L).getEffectiveAvatarUrl()).build()), mainMsg, 20);
                    return true;
                case "servers":
                    try {
                        ResponseBody body = MinehutBot.get().getHttpClient().newCall(new Request.Builder()
                                .url("https://pocket.minehut.com/network/simple_stats")
                                .header("accept", "application/json").build())
                                .execute().body();

                        if (body != null) {
                            JSONObject networkValues = new JSONObject(body.string());

                            Chat.editMessage(Chat.respondMessage(member, embed.clearFields()
                                    .setAuthor("Network Statistics", "https://minehut.com")
                                    .addField("Player Count", "" + networkValues.getInt("player_count"), true)
                                    .addField("Online Servers", "" + networkValues.getInt("server_count"), true)
                                    .setColor(Chat.CUSTOM_GREEN).build()), mainMsg, 20);
                            return true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Chat.editMessage(Chat.respondMessage(member, embed.clearFields().setAuthor("Active Network Servers", "https://minehut.com", Bot.getLogoUrl())
                            .setDescription("\nUnable to load network servers. Please try again later\n")
                            .setColor(Chat.CUSTOM_RED).build()), mainMsg, 10);
                    return true;
            }
        }

        return false;
    }
}
