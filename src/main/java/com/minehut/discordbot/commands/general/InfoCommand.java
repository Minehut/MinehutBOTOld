package com.minehut.discordbot.commands.general;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.music.VideoThread;
import com.sun.management.OperatingSystemMXBean;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * Created by MatrixTunnel on 1/29/2017.
 */
public class InfoCommand implements Command {

    @Override
    public String getCommand() {
        return "info";
    }

    private Runtime runtime = Runtime.getRuntime();
    private String getMb(long bytes) {
        return (bytes / 1024 / 1024) + "MB";
    }

    @Override
    public void onCommand(IShard shard, IGuild guild, IChannel channel, IUser sender, IMessage message, String[] args) throws DiscordException {
        Chat.removeMessage(message);

        Chat.sendMessage(Chat.getEmbed().withColor(Chat.CUSTOM_GREEN)
                .withAuthorIcon(Core.getDiscord().getApplicationIconURL()).withAuthorName(Core.getDiscord().getApplicationName()).withAuthorUrl("https://www.minehut.com")
                .appendField("Memory Usage: ", getMb(runtime.totalMemory() - runtime.freeMemory()), true)
                .appendField("Memory Free: ", getMb(runtime.freeMemory()), true)
                .appendField("Total Memory: ", getMb(runtime.totalMemory()), true)
                .appendField("Video threads: ", String.valueOf(VideoThread.VIDEO_THREADS.activeCount()), true)
                .appendField("Total threads: ", String.valueOf(Thread.getAllStackTraces().size()), true)
                .appendField("CPU Usage: ", ((int) (ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getSystemCpuLoad() * 10000)) / 100f + "%", true)
                .appendField("Discord4J Version: ", Discord4J.VERSION, true)
                .withFooterText("System time: " + new Date().toString()), message.getChannel(), 15);
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

}
