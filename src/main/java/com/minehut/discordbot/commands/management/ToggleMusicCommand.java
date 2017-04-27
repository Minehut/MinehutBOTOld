package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.exceptions.CommandException;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;

/**
 * Created by MatrixTunnel on 4/17/2017.
 */
public class ToggleMusicCommand extends Command {

    public ToggleMusicCommand() {
        super("togglemusic", CommandType.TRUSTED, "");
    }

    public static HashMap<String, Boolean> canQueue;

    @Override
    public boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        if (canQueue.get(guild.getId()) == null) {
            throw new CommandException("An unknown error while toggling music has occurred");
        }

        channel.sendMessage("**Music queueing has been " + (!canQueue.get(guild.getId()) ? "enabled" : "disabled") + " by " + sender.getAsMention() + "!**").queue();
        canQueue.put(guild.getId(), !canQueue.get(guild.getId()));

        return true;
    }

}
