package com.minehut.discordbot.commands.manage;

import com.minehut.discordbot.Core;
import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.tasks.BotTask;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

/**
 * Created by MatrixTunnel on 1/14/2017.
 */
public class ReconnectVoiceCommand implements Command {

    @Override
    public String getCommand() {
        return "reconnect";
    }

    @Override
    public void onCommand(JDA jda, Guild guild, TextChannel channel, Member member, User sender, Message message, String[] args) {
        Chat.removeMessage(message);

        guild.getAudioManager().closeAudioConnection();
        new BotTask("Reconnect voice channel " + Core.getClient().getGuildById(guild.getId()).getSelfMember().getVoiceState().getChannel().toString()) {
            @Override
            public void run() {
                guild.getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
            }
        }.delay(2000);
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
