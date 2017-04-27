package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.commands.CommandType;
import com.minehut.discordbot.exceptions.CommandException;
import com.minehut.discordbot.util.Chat;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by MatrixTunnel on 1/14/2017.
 */
public class JoinCommand extends Command {

    public JoinCommand() {
        super("join", new String[]{"summon"}, "", CommandType.TRUSTED);
    }

    @Override
    public boolean onCommand(Guild guild, TextChannel channel, Member sender, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        if (guild.getSelfMember().hasPermission(sender.getVoiceState().getChannel(), Permission.VOICE_CONNECT)) {
            try {
                guild.getAudioManager().openAudioConnection(sender.getVoiceState().getChannel());
            } catch (Exception e) {
                Chat.sendMessage("Could not connect! Reason:\n```" + e.getMessage() + "```", channel, 10);
            }
        } else {
            Chat.sendMessage("I don't have permission to connect to that channel!", channel, 10);
        }

        return true;
    }

}
