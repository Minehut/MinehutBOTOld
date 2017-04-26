package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;

/**
 * Created by MatrixTunnel on 4/17/2017.
 */
public class ToggleMusicCommand implements Command {

    @Override
    public String getCommand() {
        return "togglemusic";
    }

    public static HashMap<String, Boolean> canQueue;

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message);

        if (canQueue.get(guild.getId()) == null) {
            Core.log.info("wkdjlakjwdlkawjdkjwadlkjwhdljkhwd");
            return;
        }

        channel.sendMessage("**Music queueing has been " + (!canQueue.get(guild.getId()) ? "enabled" : "disabled") + " by " + sender.getAsMention() + "!**").queue();
        canQueue.put(guild.getId(), !canQueue.get(guild.getId()));
    }

    @Override
    public CommandType getType() {
        return CommandType.TRUSTED;
    }
}
