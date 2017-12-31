package com.minehut.discordbot.commands.management;

import com.minehut.discordbot.commands.Command;
import com.minehut.discordbot.util.Chat;
import com.minehut.discordbot.util.UserClient;
import com.minehut.discordbot.util.exceptions.CommandException;
import com.minehut.discordbot.util.tasks.BotTask;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

/**
 * Created by MatrixTunnel on 1/14/2017.
 */
public class ReconnectVoiceCommand extends Command {

    public ReconnectVoiceCommand() {
        super(CommandType.TRUSTED, null, "reconnect");
    }

    @Override
    public boolean onCommand(UserClient sender, Guild guild, TextChannel channel, Message message, String[] args) throws CommandException {
        Chat.removeMessage(message);
        Member member = guild.getMember(sender.getUser());

        if (guild.getAudioManager().isConnected()) {
            VoiceChannel vc = guild.getAudioManager().getConnectedChannel();
            guild.getAudioManager().closeAudioConnection();
            new BotTask("ReconnectVC-" + vc.getId()) {
                @Override
                public void run() {
                    if (!connect(vc))
                        Chat.sendMessage(member.getAsMention() + " I don't have permission to connect to that channel!", channel, 10);
                }
            }.delay(2000);
        } else {
            if (member.getVoiceState().inVoiceChannel()) {
                if (!connect(member.getVoiceState().getChannel()))
                    Chat.sendMessage(member.getAsMention() + " I don't have permission to connect to that channel!", channel, 10);
            } else {
                Chat.sendMessage(member.getAsMention() + " You are not in a voice channel!", channel, 10);
            }
        }

        return true;
    }

    private boolean connect(VoiceChannel channel) {
        if (channel.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
            channel.getGuild().getAudioManager().openAudioConnection(channel);
            return true;
        }
        return false;
    }

}
