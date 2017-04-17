package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by MatrixTunnel on 4/17/2017.
 */
public class ToggleMusicCommand implements Command {

    @Override
    public String getCommand() {
        return "togglemusic";
    }

    public static boolean canQueue = true;

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message);

        if (canQueue) {
            channel.sendMessage("**Music queueing was disabled by " + sender.getAsMention() + "!**").queue();
            canQueue = false;
        } else {
            channel.sendMessage("**Music queueing was enabled by " + sender.getAsMention() + "!**").queue();
            canQueue = true;
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.TRUSTED;
    }
}
