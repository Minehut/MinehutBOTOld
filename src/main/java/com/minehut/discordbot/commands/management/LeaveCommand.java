package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.exceptions.CommandException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by MatrixTunnel on 1/14/2017.
 */
public class LeaveCommand extends Command {

    public LeaveCommand() {
        super("leave", CommandType.TRUSTED, null, "goaway");
    }

    @Override
    public boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        try {
            guild.getAudioManager().closeAudioConnection();
        } catch (Exception e) {
            Chat.sendMessage("Could not leave! Reason:\n```" + e.getMessage() + "```", channel, 10);
        }

        return true;
    }

}
