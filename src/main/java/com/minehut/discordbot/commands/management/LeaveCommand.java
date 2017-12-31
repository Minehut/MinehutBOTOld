package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by MatrixTunnel on 1/14/2017.
 */
public class LeaveCommand extends Command {

    public LeaveCommand() {
        super(CommandType.TRUSTED, null, "leave", "goaway");
    }

    @Override
    public boolean onCommand(UserClient sender, Guild guild, TextChannel channel, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        if (guild.getAudioManager().isConnected()) {
            guild.getAudioManager().closeAudioConnection();
        } else {
            Chat.sendMessage(guild.getMember(sender.getUser()).getAsMention() + " I am not in a voice channel!", channel, 10);
        }

        return true;
    }

}
