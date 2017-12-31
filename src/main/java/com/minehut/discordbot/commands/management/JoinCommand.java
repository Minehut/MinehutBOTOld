package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.exceptions.CommandException;
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
        super(CommandType.TRUSTED, null, "join", "summon");
    }

    @Override
    public boolean onCommand(UserClient sender, Guild guild, TextChannel channel, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);

        Member member = guild.getMember(sender.getUser());

        if (member.getVoiceState().inVoiceChannel()) {
            if (guild.getSelfMember().hasPermission(member.getVoiceState().getChannel(), Permission.VOICE_CONNECT)) {

                guild.getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
            } else {
                Chat.sendMessage(member.getAsMention() + " I don't have permission to connect to that channel!", channel, 10);
            }
        } else {
            Chat.sendMessage(member.getAsMention() + " You are not in a voice channel!", channel, 10);
        }

        return true;
    }

}
