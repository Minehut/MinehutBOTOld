package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.exceptions.CommandException;
import com.minehut.discordbot.util.tasks.BotTask;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by MatrixTunnel on 1/14/2017.
 */
public class ReconnectVoiceCommand extends Command {

    public ReconnectVoiceCommand() {
        super("reconnect", CommandType.TRUSTED, null);
    }

    @Override
    public boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        guild.getAudioManager().closeAudioConnection();
        new BotTask("ReconnectVoiceChannel") {
            @Override
            public void run() {
                guild.getAudioManager().openAudioConnection(sender.getVoiceState().getChannel());
            }
        }.delay(2000);

        return true;
    }

}
