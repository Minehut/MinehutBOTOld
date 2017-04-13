package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.tasks.BotTask;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by MatrixTunnel on 1/14/2017.
 */
public class ReconnectVoiceCommand implements Command {

    @Override
    public String getCommand() {
        return "reconnect";
    }

    @Override
    public void onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) {
        Chat.removeMessage(message);

        guild.getAudioManager().closeAudioConnection();
        new BotTask("ReconnectVoiceChannel") {
            @Override
            public void run() {
                guild.getAudioManager().openAudioConnection(sender.getVoiceState().getChannel());
            }
        }.delay(2000);
    }

    @Override
    public CommandType getType() {
        return CommandType.TRUSTED;
    }
}
