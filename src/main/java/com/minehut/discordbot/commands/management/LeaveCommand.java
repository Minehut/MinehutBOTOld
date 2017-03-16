package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

/**
 * Created by MatrixTunnel on 1/14/2017.
 */
public class LeaveCommand implements Command {

    @Override
    public String getCommand() {
        return "leave";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"goaway"};
    }

    @Override
    public void onCommand(JDA jda, Guild guild, TextChannel channel, Member member, User sender, Message message, String[] args) {
        Chat.removeMessage(message);

        try {
            guild.getAudioManager().closeAudioConnection();
        } catch (Exception e) {
            Chat.sendMessage("Could not leave! Reason:\n```" + e.getMessage() + "```", channel, 10);
        }
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMINISTRATIVE;
    }
}
