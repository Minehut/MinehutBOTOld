package com.minehut.discordbot.commands.manage;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

/**
 * Created by MatrixTunnel on 1/14/2017.
 */
public class JoinCommand implements Command {

    @Override
    public String getCommand() {
        return "join";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"summon"};
    }

    @Override
    public void onCommand(JDA jda, Guild guild, TextChannel channel, Member member, User sender, Message message, String[] args) {
        Chat.removeMessage(message);

        if (guild.getSelfMember().hasPermission(member.getVoiceState().getChannel(), Permission.VOICE_CONNECT)) {
            try {
                guild.getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
            } catch (Exception e) {
                Chat.sendMessage("Could not connect! Reason:\n```" + e.getMessage() + "```", channel, 10);
            }
        } else {
            Chat.sendMessage("I don't have permission to connect to that channel!", channel, 10);
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
