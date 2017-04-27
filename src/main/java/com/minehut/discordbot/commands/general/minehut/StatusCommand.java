package com.minehut.discordbot.commands.general.minehut;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Bot;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.URLJson;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MatrixTunnel on 4/14/2017.
 * Matcher code/regex made by MeowingTwurtle.
 * API by ReduxRedstone.
 */
public class StatusCommand extends Command {

    public StatusCommand() {
        super("status", CommandType.GENERAL, "<network|bot>");
    }

    @Override
    public boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        EmbedBuilder embed = Chat.getEmbed();

        if (args.length == 1) {
            Message mainMsg = channel.sendMessage(new MessageBuilder().setEmbed(embed.addField("Gathering Information...", "This may take a few moments", true)
                    .setColor(Chat.CUSTOM_ORANGE).build()).build()).complete();

            switch (args[0]) {
                case "network":
                    StringBuilder motd = new StringBuilder();

                    try {
                        JSONObject status = new URLJson("https://minehut.com/api/status/").getJsonObject();

                        embed.clearFields()
                                .addField("Users Online", status.getJSONObject("ping").getJSONObject("players").get("online") + "/" +
                                        status.getJSONObject("ping").getJSONObject("players").get("max"), true)
                                .addField("Servers Online", status.getInt("totalPlayerServerCount") + "/" + status.getInt("totalPlayerMaxServerCount"), true)
                                .addField("Ram Usage", (status.getInt("totalPlayerServerRamUsage") / 1024) + "/" + status.getInt("totalPlayerServerMaxRam") + " GB", true)
                                .addField("Players on Player Servers", String.valueOf(status.getInt("totalPlayerServerPlayerCount")), true);

                        Matcher matcher = Pattern.compile("(?<=\"text\":\").*?(?=\")").matcher(String.valueOf(status.getJSONObject("ping").getJSONObject("description")));

                        while (matcher.find()) {
                            motd.append(matcher.group());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Chat.editMessage(embed.clearFields().setAuthor("Minehut Network Status", "https://minehut.com", Bot.getLogo())
                                .setDescription("\nThe network is down at this time. Please try again later\n")
                                .setColor(Chat.CUSTOM_RED).build(), mainMsg, 15);
                        return true;
                    }

                    Chat.editMessage(embed.setDescription("`" + motd.toString().replaceAll("§(.)", "") + "`").setAuthor("Minehut Network Status", "https://minehut.com", Bot.getLogo())
                            .setFooter("System time | " + Bot.getBotTime(), null).setColor(Chat.CUSTOM_GREEN).build(), mainMsg, 20);
                    break;
                case "bot":
                    Runtime runtime = Runtime.getRuntime();
                    Chat.editMessage(Chat.getEmbed().clearFields()
                            .setAuthor(Core.getClient().getSelfUser().getName() + " Info", "https://minehut.com", Core.getClient().getSelfUser().getAvatarUrl())
                            .addField("Memory Usage", Bot.getMb(runtime.totalMemory() - runtime.freeMemory()), true)
                            .addField("Memory Free", Bot.getMb(runtime.freeMemory()), true)
                            .addField("Total Memory", Bot.getMb(runtime.totalMemory()), true)
                            .addField("Video Threads", String.valueOf(VideoThread.VIDEO_THREADS.activeCount()), true)
                            .addField("Total Threads", String.valueOf(Thread.getAllStackTraces().size()), true)
                            .addField("Total Cores", String.valueOf(runtime.availableProcessors()), true)
                            .addField("CPU Usage", ((int) (ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getSystemCpuLoad() * 10000)) / 100f + "%", true)
                            .addField("Lavaplayer Version", PlayerLibrary.VERSION, true)
                            .addField("JDA Version", JDAInfo.VERSION, true)
                            .setColor(Chat.CUSTOM_GREEN).setFooter("System time | " + Bot.getBotTime(), null).build(), mainMsg, 20);
                    break;
                case "hosts":
                    if (!sender.getUser().equals(Bot.getCreator())) {
                        return false;
                    }

                    Integer playersOnline = 0, usedRam = 0, totalRam = 0;
                    try {
                        JSONArray hosts = new URLJson("http://mctoolbox.me/minehut/hosts/?token=" + Core.getConfig().getSecretKey()).getJsonArray();
                        embed.clearFields();

                        for (Object host : hosts) {
                            JSONObject obj = (JSONObject) host;

                            embed.addField(obj.get("ip").toString(),
                                    "**Servers:** `" + obj.getInt("server_count") + "/" + obj.getInt("max_servers") + "`" +
                                            "\n**Ram Usage:** `" + (obj.getInt("ram_usage") / 1024) + "/" + obj.getInt("max_ram") + "` GB" +
                                            "\n**Players Online:** `" + obj.getInt("player_count") + "/" + obj.getInt("max_players") + "`", true);
                            playersOnline = playersOnline + obj.getInt("player_count");
                            usedRam = usedRam + obj.getInt("ram_usage");
                            totalRam = totalRam + obj.getInt("max_ram");
                        }

                        embed.setAuthor("Minehut Network Status", "https://minehut.com", Bot.getLogo())
                                .setDescription("**Total Hosts:** `" + hosts.length() + "`" +
                                        "\n**Total Ram Usage:** `" + (usedRam / 1024) + "/" + totalRam + "` GB" +
                                        "\n**Players On Servers:** `" + playersOnline + "`");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Chat.editMessage(embed.clearFields().setAuthor("Minehut Network Status", "https://minehut.com", Bot.getLogo())
                                .setDescription("\nThe network is down at this time. Please try again later\n").setColor(Chat.CUSTOM_RED).build(), mainMsg, 10);
                        return true;
                    }

                    Chat.editMessage(embed.setFooter("System time | " + Bot.getBotTime(), null)
                            .setColor(Chat.CUSTOM_GREEN).build(), mainMsg, 20);
                    break;
                default:
                    return false;
            }
        } else {
            return false;
        }

        return false;
    }
}
