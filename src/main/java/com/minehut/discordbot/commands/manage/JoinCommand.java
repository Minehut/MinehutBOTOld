package com.minehut.discordbot.commands.manage;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.JDA;
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

        guild.getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
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
